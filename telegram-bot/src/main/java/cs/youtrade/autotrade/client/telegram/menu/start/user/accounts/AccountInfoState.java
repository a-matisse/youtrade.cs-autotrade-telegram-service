package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.AccountExportResponseDto;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountInfoState extends YTPTextMenuState<AccountInfoMenu> {
    private final AccountsV2Endpoint endpoint;
    private final YTPPageProcessor pageProcessor;
    private final Map<Long, Data> registry = new ConcurrentHashMap<>();

    public AccountInfoState(UserTextMessageSender sender, AccountsV2Endpoint endpoint, YTPPageProcessor pageProcessor) {
        super(sender);
        this.endpoint = endpoint;
        this.pageProcessor = pageProcessor;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_INFO; }

    @Override
    public AccountInfoMenu getOption(String optionStr) { return AccountInfoMenu.valueOf(optionStr); }

    @Override
    public AccountInfoMenu[] getOptions(UserData user) { return AccountInfoMenu.values(); }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, AccountInfoMenu option) {
        registry.remove(user.getChatId());
        return UserMenu.ACCOUNTS_MORE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        if (!user.isAccountExportPasswordSet())
            return DynamicEmoji.YOUTRADE.getEmoji() + " <i>Данные аккаунта</i>\n\n"
                    + DynamicEmoji.SECURE.getEmoji() + " <b>Доступ</b>\n"
                    + "<blockquote>• Защитный пароль: <b>не установлен</b></blockquote>\n\n"
                    + DynamicEmoji.WRITE.getEmoji() + " <b>Установите пароль в /prefs</b>";
        Data data = data(user);
        String summary = String.format("""
                        %s <i>Данные аккаунта</i>

                        %s <b>Аккаунт</b>
                        <blockquote>• ID: %s
                        • Защитный пароль: %s</blockquote>

                        """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.STEAM.getEmoji(),
                data.tokenId == null ? "—" : "<code>" + data.tokenId + "</code>",
                data.passwordVerified ? "✅ Проверен" : "—");
        if (data.stage == Stage.PASSWORD)
            return summary + DynamicEmoji.WRITE.getEmoji() + " <b>Отправьте защитный пароль</b>";
        if (data.stage == Stage.DONE)
            return summary + data.result;
        try {
            return summary + DynamicEmoji.WRITE.getEmoji() + " <b>Отправьте ID аккаунта</b>\n"
                    + "<blockquote expandable>" + pageProcessor.getPage(user.getChatId()).getAccountsListStr() + "</blockquote>";
        } catch (RuntimeException error) {
            return summary + "Не удалось загрузить аккаунты. Попробуйте снова.";
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!user.isAccountExportPasswordSet()) return supportedState();
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        Data data = data(user);
        if (data.stage == Stage.DONE) return supportedState();
        if (data.stage == Stage.SELECT) {
            try {
                long id = Long.parseLong(update.getMessage().getText().trim());
                var page = pageProcessor.getPage(user.getChatId());
                if (page.fcd().getAccounts().stream().noneMatch(account -> account.getId().equals(id))) {
                    sender.sendTextMes(bot, user, "Аккаунт не найден на текущей странице.");
                    return supportedState();
                }
                data.tokenId = id;
                data.stage = Stage.PASSWORD;
            } catch (NumberFormatException error) {
                sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
            } catch (RuntimeException error) {
                sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
            }
            return supportedState();
        }
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        String password = update.getMessage().getText();
        if (password.isEmpty()) {
            sender.sendTextMes(bot, user, "Защитный пароль не может быть пустым.");
            return supportedState();
        }
        try {
            var answer = endpoint.exportCredentials(user.getChatId(), data.tokenId, password);
            if (answer.getStatus() == 200 && answer.getResponse() != null && answer.getResponse().getAccount() != null) {
                sender.sendTextMes(bot, user, format(answer.getResponse()));
                data.passwordVerified = true;
                data.stage = Stage.DONE;
                data.result = DynamicEmoji.SUCCESS.getEmoji() + " <b>Данные отправлены отдельным сообщением</b>";
                return supportedState();
            }
            if (answer.getStatus() == 429) {
                data.stage = Stage.DONE;
                data.result = DynamicEmoji.WARNING.getEmoji() + " <b>Слишком много попыток. Повторите через 15 минут.</b>";
                return supportedState();
            }
            sender.sendTextMes(bot, user, switch (answer.getStatus()) {
                case 403 -> "Неверный защитный пароль.";
                case 404 -> "Аккаунт не найден или больше вам не принадлежит.";
                case 502 -> "Не удалось получить данные Steam. Попробуйте позже.";
                default -> "Не удалось получить данные аккаунта. Попробуйте позже.";
            });
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, "Не удалось получить данные аккаунта. Попробуйте позже.");
        }
        return supportedState();
    }

    private String format(AccountExportResponseDto response) {
        var account = response.getAccount();
        String accountDetails = lines(
                "• ID: <code>" + account.getSteamTokenId() + "</code>",
                field("Имя", account.getGivenName()),
                field("Steam ID", account.getSteamId64()),
                field("Параметры", account.getParametersName()),
                account.getSource() != null && account.getDestination() != null
                        ? "• Маршрут: <b>" + escape(market(account.getSource()))
                                + " → " + escape(market(account.getDestination())) + "</b>"
                        : "");
        String keys = lines(
                secret("Ключ Y.CS", response.getUserApiKey()),
                secret("Ключ источника", account.getSourceApiKey()),
                secret("Ключ покупки", account.getBuyApiKey()),
                secret("Ключ продажи", account.getSellApiKey()),
                secret("Steam Partner", account.getSteamPartner()),
                secret("Steam Trade Token", account.getSteamTradeToken()));
        String steam = lines(
                secret("Логин Steam", account.getSteamLogin()),
                secret("Пароль Steam", account.getSteamPassword()),
                secret("Guard", account.getGuardCode()),
                steamStatus(account.getCredentialsStatus()));
        String notice = response.isComplete() ? "" : "\n└ <i>Данные Steam доступны не полностью."
                + (account.getCredentialsError() == null ? ""
                : " Причина: " + escape(account.getCredentialsError())) + "</i>";

        return String.format("""
                        %s <i>Данные аккаунта</i>

                        %s <b>Аккаунт</b>
                        <blockquote>%s</blockquote>

                        %s <b>Ключи и обмен</b>
                        <blockquote expandable>%s</blockquote>

                        %s <b>Steam</b>
                        <blockquote>%s</blockquote>%s
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                DynamicEmoji.STEAM.getEmoji(), accountDetails,
                DynamicEmoji.KEY.getEmoji(), keys,
                DynamicEmoji.SECURE.getEmoji(), steam, notice);
    }

    private String market(String value) {
        try {
            return MarketType.valueOf(value).getMarketName();
        } catch (IllegalArgumentException error) {
            return value.replace('_', ' ');
        }
    }

    private String lines(String... values) {
        return Stream.of(values).filter(value -> !value.isBlank()).collect(Collectors.joining("\n"));
    }

    private String field(String label, String value) {
        return value == null || value.isBlank() ? "" : "• " + label + ": <code>" + escape(value) + "</code>";
    }

    private String secret(String label, String value) {
        return value == null || value.isBlank() ? ""
                : "• " + label + ": <tg-spoiler><code>" + escape(value) + "</code></tg-spoiler>";
    }

    private String steamStatus(String status) {
        if ("NOT_CONNECTED".equals(status)) return "• Воркер не подключён";
        if ("UNAVAILABLE".equals(status)) return "• Данные Steam временно недоступны";
        return "";
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private Data data(UserData user) {
        return registry.computeIfAbsent(user.getChatId(), _ -> new Data());
    }

    private enum Stage { SELECT, PASSWORD, DONE }

    private static class Data {
        private volatile Stage stage = Stage.SELECT;
        private Long tokenId;
        private boolean passwordVerified;
        private String result;
    }
}
