package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.AccountExportResponseDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountInfoResultState extends AbstractAccountInfoState {
    public AccountInfoResultState(UserTextMessageSender sender, AccountInfoRegistry registry) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_INFO_RESULT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.getOrCreate(user, AccountInfoData::new);
        if (data.getResult() == null)
            return "Не удалось получить результат запроса данных аккаунта.";
        String result = data.getResult() == AccountInfoData.Result.SENT
                ? String.format("""
                        %s <b>Данные отправлены отдельным сообщением</b>
                        """, DynamicEmoji.SUCCESS.getEmoji())
                : String.format("""
                        %s <b>Слишком много попыток. Повторите через 15 минут.</b>
                        """, DynamicEmoji.WARNING.getEmoji());
        return String.format("""
                %s%s
                """, summary(user), result);
    }

    public void sendDetails(TelegramClient bot, UserData user, AccountExportResponseDto response) {
        sender.sendTextMes(bot, user, format(response));
    }

    private String format(AccountExportResponseDto response) {
        var account = response.getAccount();
        String accountDetails = lines(
                "• ID: <code>%s</code>".formatted(account.getSteamTokenId()),
                field("Имя", account.getGivenName()),
                field("Steam ID", account.getSteamId64()),
                field("Параметры", account.getParametersName()),
                account.getSource() != null && account.getDestination() != null
                        ? "• Маршрут: <b>%s → %s</b>".formatted(escape(market(account.getSource())),
                        escape(market(account.getDestination())))
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
        String reason = account.getCredentialsError() == null ? ""
                : " Причина: %s".formatted(escape(account.getCredentialsError()));
        String notice = response.isComplete() ? "" : String.format("""

                └ <i>Данные Steam доступны не полностью.%s</i>
                """, reason).stripTrailing();

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

    private static String market(String value) {
        try {
            return MarketType.valueOf(value).getMarketName();
        } catch (IllegalArgumentException error) {
            return value.replace('_', ' ');
        }
    }

    private static String lines(String... values) {
        return Stream.of(values)
                .filter(value -> !value.isBlank())
                .collect(Collectors.joining("\n"));
    }

    private static String field(String label, String value) {
        return value == null || value.isBlank()
                ? ""
                : "• %s: <code>%s</code>".formatted(label, escape(value));
    }

    private static String secret(String label, String value) {
        return value == null || value.isBlank()
                ? ""
                : "• %s: <tg-spoiler><code>%s</code></tg-spoiler>".formatted(label, escape(value));
    }

    private static String steamStatus(String status) {
        if ("NOT_CONNECTED".equals(status))
            return "• Воркер не подключён";
        if ("UNAVAILABLE".equals(status))
            return "• Данные Steam временно недоступны";
        return "";
    }

    private static String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
