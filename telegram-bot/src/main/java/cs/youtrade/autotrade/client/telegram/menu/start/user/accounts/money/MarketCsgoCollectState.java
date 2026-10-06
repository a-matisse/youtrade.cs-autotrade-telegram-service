package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdMarketCsgoBalanceTransferDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.MarketCsgoBalanceEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdMarketCsgoBalanceTransferInput;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketCsgoCollectState extends YTPTextMenuState<MarketCsgoCollectMenu> {
    private static final long FORM_TIMEOUT_MILLIS = 15 * 60 * 1000;
    private final MarketCsgoBalanceEndpoint endpoint;
    private final MarketCsgoAccountLookup lookup;
    private final Map<Long, Data> registry = new ConcurrentHashMap<>();

    public MarketCsgoCollectState(UserTextMessageSender sender, MarketCsgoBalanceEndpoint endpoint,
                                  MarketCsgoAccountLookup lookup) {
        super(sender);
        this.endpoint = endpoint;
        this.lookup = lookup;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_COLLECT; }

    @Override
    public MarketCsgoCollectMenu getOption(String optionStr) { return MarketCsgoCollectMenu.valueOf(optionStr); }

    @Override
    public MarketCsgoCollectMenu[] getOptions(UserData userData) { return MarketCsgoCollectMenu.values(); }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, MarketCsgoCollectMenu option) {
        Data data = registry.get(user.getChatId());
        if (data != null && data.stage == Stage.PROCESSING) return supportedState();
        registry.remove(user.getChatId());
        return UserMenu.ACCOUNTS;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        Data data = data(user);
        if (data.stage == Stage.SELECT) {
            try {
                if (!lookup.isMarketCsgo(user.getChatId()))
                    return "Перевод баланса доступен только для направления Market.CSGO.";
            } catch (RuntimeException error) {
                return "Не удалось проверить направление торговли. Попробуйте ещё раз.";
            }
        }
        String account = data.tokenId == null ? "—" : "<code>" + data.tokenId + "</code>";
        String summary = String.format("""
                        %s <i>Сбор баланса Market.CSGO</i>

                        %s <b>Получатель</b>
                        <blockquote>• Аккаунт: %s
                        • Пароль: %s</blockquote>

                        """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                account, data.stage == Stage.SELECT ? "—" : data.stage == Stage.PASSWORD ? "ожидается" : "✅ Получен");
        return summary + switch (data.stage) {
            case SELECT -> String.format("""
                            %s <b>Отправьте ID аккаунта, на который собрать балансы</b>
                            <blockquote expandable>%s</blockquote>
                            """, DynamicEmoji.WRITE.getEmoji(), lookup.currentPageBalances(user.getChatId()));
            case PASSWORD -> String.format("""
                            %s <b>Отправьте платёжный пароль Market.CSGO</b>
                            <blockquote>Пароль должен быть единым для всех аккаунтов. При разных паролях сбор остановится; уже подтверждённые переводы останутся выполненными.</blockquote>
                            """, DynamicEmoji.WRITE.getEmoji());
            case PROCESSING -> String.format("%s <b>Собираем балансы...</b>", DynamicEmoji.WAIT.getEmoji());
            case DONE -> data.result;
        };
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        Data data = data(user);
        if (data.stage == Stage.SELECT) {
            try {
                if (!lookup.isMarketCsgo(user.getChatId())) {
                    sender.sendTextMes(bot, user, "Перевод баланса доступен только для направления Market.CSGO.");
                    return supportedState();
                }
                long id = Long.parseLong(update.getMessage().getText().trim());
                var account = lookup.find(user.getChatId(), id);
                if (account == null || account.available() == null) {
                    sender.sendTextMes(bot, user, "Аккаунт с продажей Market.CSGO не найден в текущих параметрах.");
                    return supportedState();
                }
                data.tokenId = id;
                data.stage = Stage.PASSWORD;
            } catch (NumberFormatException error) {
                sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
            } catch (RuntimeException error) {
                sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
            }
        } else if (data.stage == Stage.PASSWORD) {
            sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
            String password = update.getMessage().getText().trim();
            if (password.isEmpty()) {
                sender.sendTextMes(bot, user, "Платёжный пароль не может быть пустым.");
                return supportedState();
            }
            data.stage = Stage.PROCESSING;
            FcdMarketCsgoBalanceTransferInput input = new FcdMarketCsgoBalanceTransferInput();
            input.setDestinationTokenId(data.tokenId);
            input.setPaymentPassword(password);
            try {
                var answer = endpoint.balanceTransfer(user.getChatId(), input);
                data.result = formatResult(answer.getStatus() >= 300 ? null : answer.getResponse());
            } catch (RuntimeException error) {
                data.result = "Не удалось получить результат сбора. Перед новым запросом проверьте балансы и историю Market.CSGO.";
            } finally {
                input.setPaymentPassword(null);
                data.stage = Stage.DONE;
            }
            try { lookup.find(user.getChatId(), data.tokenId); } catch (RuntimeException ignored) { }
        }
        return supportedState();
    }

    private String formatResult(FcdMarketCsgoBalanceTransferDto result) {
        if (result == null)
            return "Не удалось получить результат сбора. Перед новым запросом проверьте балансы и историю Market.CSGO.";
        int transferred = result.getTransferredTokens();
        StringBuilder text = new StringBuilder(String.format("%s <b>%s</b>\n",
                result.isResult() ? DynamicEmoji.SUCCESS.getEmoji() : DynamicEmoji.WARNING.getEmoji(),
                result.isResult() ? "Сбор завершён" : "Сбор остановлен"));
        text.append("<blockquote>• Подтверждено переводов: <b>").append(transferred).append("</b>");
        if (result.getTransferredAmounts() != null)
            result.getTransferredAmounts().forEach((currency, amount) ->
                    text.append("\n• Переведено: <b>").append(amount.toPlainString()).append(" ")
                            .append(currency).append("</b>"));
        BigDecimal balance = result.getDestinationBalanceAfter();
        if (balance != null) {
            text.append("\n• Баланс получателя: <b>").append(balance.toPlainString()).append(" ")
                    .append(result.getCurrency() == null ? "" : result.getCurrency()).append("</b>");
            if ("CALCULATED".equals(result.getBalanceSource())) text.append(" <i>(по расчёту)</i>");
            if ("DATABASE_FALLBACK".equals(result.getBalanceSource()))
                text.append(" <i>(расчёт с балансом из БД)</i>");
        }
        text.append("</blockquote>");
        if (result.getAccounts() != null) {
            var unknown = result.getAccounts().stream().filter(a -> "UNKNOWN".equals(a.getStatus())).toList();
            if (!unknown.isEmpty())
                text.append("\n<i>Есть переводы с неизвестным результатом. Не повторяйте сбор до проверки истории Market.CSGO.</i>");
            if (!result.getAccounts().isEmpty()) {
                text.append("\n<blockquote expandable><b>По аккаунтам</b>");
                result.getAccounts().stream().limit(30).forEach(account -> {
                    text.append("\n• <code>").append(account.getTokenId()).append("</code> — ");
                    if ("TRANSFERRED".equals(account.getStatus())) text.append("переведено");
                    else if ("SKIPPED_ZERO_BALANCE".equals(account.getStatus())) text.append("нулевой баланс");
                    else if ("NOT_ATTEMPTED".equals(account.getStatus())) text.append("не обработан");
                    else text.append("результат неизвестен");
                    if (account.getBalanceAfter() != null)
                        text.append(", остаток <b>").append(account.getBalanceAfter().toPlainString())
                                .append(" ").append(account.getCurrency() == null ? "" : account.getCurrency())
                                .append("</b>");
                });
                if (result.getAccounts().size() > 30)
                    text.append("\n• Ещё аккаунтов: ").append(result.getAccounts().size() - 30);
                text.append("</blockquote>");
            }
        }
        if (!result.isResult()) text.append("\n<i>Проверьте историю Market.CSGO перед повтором.</i>");
        return text.toString();
    }

    private Data data(UserData user) {
        return registry.compute(user.getChatId(), (_, previous) -> {
            long now = System.currentTimeMillis();
            if (previous == null || now - previous.lastTouchedAt > FORM_TIMEOUT_MILLIS) return new Data();
            previous.lastTouchedAt = now;
            return previous;
        });
    }

    @Scheduled(fixedDelay = 60_000)
    public void clearExpiredForms() {
        long now = System.currentTimeMillis();
        registry.forEach((chatId, data) -> {
            if (now - data.lastTouchedAt > FORM_TIMEOUT_MILLIS) registry.remove(chatId, data);
        });
    }

    private enum Stage { SELECT, PASSWORD, PROCESSING, DONE }

    private static class Data {
        private volatile Stage stage = Stage.SELECT;
        private volatile long lastTouchedAt = System.currentTimeMillis();
        private Long tokenId;
        private String result;
    }
}
