package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdMarketCsgoMoneySendDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.MarketCsgoBalanceEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdMarketCsgoMoneySendInput;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketCsgoSendState extends YTPTextMenuState<MarketCsgoSendMenu> {
    private static final long FORM_TIMEOUT_MILLIS = 15 * 60 * 1000;
    private final MarketCsgoBalanceEndpoint endpoint;
    private final MarketCsgoAccountLookup lookup;
    private final BigDecimal testAmount;
    private final Map<Long, Data> registry = new ConcurrentHashMap<>();

    public MarketCsgoSendState(UserTextMessageSender sender, MarketCsgoBalanceEndpoint endpoint,
                               MarketCsgoAccountLookup lookup,
                               @Value("${yt.market-csgo.test-transfer-divisor}") long testAmountDivisor) {
        super(sender);
        this.endpoint = endpoint;
        this.lookup = lookup;
        if (testAmountDivisor <= 0) throw new IllegalArgumentException("Invalid test transfer divisor");
        this.testAmount = BigDecimal.ONE.divide(BigDecimal.valueOf(testAmountDivisor), 3,
                java.math.RoundingMode.UNNECESSARY);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND; }

    @Override
    public MarketCsgoSendMenu getOption(String optionStr) { return MarketCsgoSendMenu.valueOf(optionStr); }

    @Override
    public MarketCsgoSendMenu[] getOptions(UserData user) {
        Data data = data(user);
        return switch (data.stage) {
            case AMOUNT -> new MarketCsgoSendMenu[]{MarketCsgoSendMenu.AMOUNT_200,
                    MarketCsgoSendMenu.AMOUNT_500, MarketCsgoSendMenu.AMOUNT_1000,
                    MarketCsgoSendMenu.ALL, MarketCsgoSendMenu.RETURN};
            case CONFIRM -> new MarketCsgoSendMenu[]{MarketCsgoSendMenu.CONFIRM, MarketCsgoSendMenu.CANCEL};
            case VERIFY -> new MarketCsgoSendMenu[]{MarketCsgoSendMenu.RECEIVED, MarketCsgoSendMenu.NOT_RECEIVED};
            default -> new MarketCsgoSendMenu[]{MarketCsgoSendMenu.RETURN};
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        Data data = data(user);
        if (data.stage == Stage.SELECT) {
            try {
                if (!lookup.isMarketCsgo(user.getChatId()))
                    return "Вывод доступен только для направления Market.CSGO.";
            } catch (RuntimeException error) {
                return "Не удалось проверить направление торговли. Попробуйте ещё раз.";
            }
        }
        String header = String.format("""
                        %s <i>Вывод с Market.CSGO</i>

                        %s <b>Перевод</b>
                        <blockquote>• Аккаунт: %s
                        • Доступно до перевода: %s
                        • Платёжный пароль: %s
                        • Получатель: %s
                        • Валюта перевода: <b>USD</b>
                        • Сумма: %s</blockquote>

                        """,
                DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                data.tokenId == null ? "—" : "<code>" + data.tokenId + "</code>",
                data.available == null ? "—" : "<b>$" + money(data.available) + "</b>",
                data.passwordProvided ? "✅ Получен" : "—",
                data.destinationProvided ? "✅ Ключ указан" : "—",
                data.amount == null ? "—" : "<b>$" + money(data.amount) + "</b>");
        return header + switch (data.stage) {
            case SELECT -> String.format("""
                            %s <b>Отправьте ID аккаунта, с которого хотите вывести деньги</b>
                            <blockquote expandable>%s</blockquote>
                            """, DynamicEmoji.WRITE.getEmoji(), lookup.currentPageBalances(user.getChatId()));
            case PASSWORD -> String.format("%s <b>Отправьте платёжный пароль Market.CSGO</b>",
                    DynamicEmoji.WRITE.getEmoji());
            case DESTINATION -> String.format("%s <b>Отправьте API-ключ аккаунта получателя</b>",
                    DynamicEmoji.WRITE.getEmoji());
            case AMOUNT -> String.format("""
                            %s <b>Выберите сумму или отправьте её сообщением</b>
                            └ <i>«Всё» — доступный баланс с учётом пробного перевода.</i>
                            """, DynamicEmoji.WRITE.getEmoji());
            case CONFIRM -> String.format("""
                            %s <b>Проверьте данные и подтвердите перевод</b>
                            <blockquote>Сначала отправим <b>$%s</b> для проверки получателя. После вашего подтверждения отправим остаток выбранной суммы.</blockquote>
                            """, DynamicEmoji.QUESTION.getEmoji(), testAmount.toPlainString());
            case PROCESSING -> String.format("%s <b>Проверяем результат перевода...</b>",
                    DynamicEmoji.WAIT.getEmoji());
            case VERIFY -> String.format("""
                            %s <b>Пробные $%s отправлены</b>
                            <blockquote>Это обязательная проверка безопасности. Убедитесь, что деньги пришли на нужный аккаунт, прежде чем отправлять остаток.</blockquote>
                            """, DynamicEmoji.WARNING.getEmoji(), testAmount.toPlainString());
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
        String value = update.getMessage().getText().trim();
        switch (data.stage) {
            case SELECT -> selectAccount(bot, user, data, value);
            case PASSWORD -> {
                sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
                if (value.isEmpty()) sender.sendTextMes(bot, user, "Платёжный пароль не может быть пустым.");
                else {
                    data.password = value;
                    data.passwordProvided = true;
                    data.stage = Stage.DESTINATION;
                }
            }
            case DESTINATION -> {
                sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
                if (value.isEmpty()) sender.sendTextMes(bot, user, "API-ключ получателя не может быть пустым.");
                else {
                    data.destinationKey = value;
                    data.destinationProvided = true;
                    data.stage = Stage.AMOUNT;
                }
            }
            case AMOUNT -> {
                try { chooseAmount(bot, user, data, new BigDecimal(value.replace(',', '.'))); }
                catch (NumberFormatException error) {
                    sender.sendTextMes(bot, user, "Не удалось распознать сумму. Пример: <code>200.00</code>");
                }
            }
            default -> { }
        }
        return supportedState();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user,
                                    MarketCsgoSendMenu option) {
        Data data = data(user);
        synchronized (data) {
            if (data.stage == Stage.PROCESSING) return supportedState();
            if (option == MarketCsgoSendMenu.RETURN || option == MarketCsgoSendMenu.CANCEL) {
                if (data.stage == Stage.VERIFY) {
                    finish(data, "Пробный перевод отправлен. Остаток не отправлен. Проверьте историю Market.CSGO и аккаунт получателя.");
                    return supportedState();
                }
                registry.remove(user.getChatId(), data);
                data.eraseSecrets();
                return UserMenu.ACCOUNTS;
            }
            if (data.stage == Stage.AMOUNT) {
                switch (option) {
                    case AMOUNT_200 -> chooseAmount(bot, user, data, new BigDecimal("200"));
                    case AMOUNT_500 -> chooseAmount(bot, user, data, new BigDecimal("500"));
                    case AMOUNT_1000 -> chooseAmount(bot, user, data, new BigDecimal("1000"));
                    case ALL -> chooseAmount(bot, user, data,
                            data.available.setScale(3, java.math.RoundingMode.DOWN));
                    default -> { }
                }
            } else if (data.stage == Stage.CONFIRM && option == MarketCsgoSendMenu.CONFIRM) {
                data.stage = Stage.PROCESSING;
                sendTest(user, data);
            } else if (data.stage == Stage.VERIFY && option == MarketCsgoSendMenu.NOT_RECEIVED) {
                finish(data, "Пробный перевод отправлен. Остаток не отправлен. Проверьте историю Market.CSGO и аккаунт получателя.");
            } else if (data.stage == Stage.VERIFY && option == MarketCsgoSendMenu.RECEIVED) {
                data.stage = Stage.PROCESSING;
                sendRemaining(user, data);
            }
        }
        return supportedState();
    }

    private void selectAccount(TelegramClient bot, UserData user, Data data, String value) {
        try {
            if (!lookup.isMarketCsgo(user.getChatId())) {
                sender.sendTextMes(bot, user, "Вывод доступен только для направления Market.CSGO.");
                return;
            }
            long id = Long.parseLong(value);
            var account = lookup.find(user.getChatId(), id);
            if (account == null || account.available() == null) {
                sender.sendTextMes(bot, user, "Аккаунт с продажей Market.CSGO не найден в текущих параметрах.");
                return;
            }
            if (account.available().signum() <= 0) {
                sender.sendTextMes(bot, user, "С этого аккаунта нечего переводить: доступный баланс равен нулю.");
                return;
            }
            if (account.available().compareTo(testAmount) < 0) {
                sender.sendTextMes(bot, user, "Баланса недостаточно для пробного перевода $" + testAmount.toPlainString() + ".");
                return;
            }
            data.tokenId = id;
            data.available = account.available();
            data.stage = Stage.PASSWORD;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
        }
    }

    private void chooseAmount(TelegramClient bot, UserData user, Data data, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 3) {
            sender.sendTextMes(bot, user, "Сумма должна быть положительной и содержать не более трёх знаков после запятой.");
            return;
        }
        if (amount.compareTo(testAmount) < 0) {
            sender.sendTextMes(bot, user, "Минимальная сумма с проверкой получателя — $" + testAmount.toPlainString() + ".");
            return;
        }
        if (amount.compareTo(data.available) > 0) {
            sender.sendTextMes(bot, user, "Сумма больше доступного баланса: $" + money(data.available) + ".");
            return;
        }
        data.amount = amount;
        data.stage = Stage.CONFIRM;
    }

    private void sendTest(UserData user, Data data) {
        FcdMarketCsgoMoneySendDto result = send(user, data, testAmount);
        if (result == null || result.isOutcomeUnknown()) {
            finish(data, "Результат пробного перевода неизвестен. Остаток не отправлен. Проверьте историю Market.CSGO перед новой попыткой.");
        } else if (!result.isResult()) {
            finish(data, "Пробный перевод не удался. Остаток не отправлен. Проверьте баланс, валюту и данные получателя.");
        } else {
            data.stage = Stage.VERIFY;
        }
    }

    private void sendRemaining(UserData user, Data data) {
        BigDecimal remaining = data.amount.subtract(testAmount);
        if (remaining.signum() == 0) {
            finish(data, String.format("%s <b>Перевод завершён</b>\n<blockquote>Отправлено: <b>$%s</b>, включая пробные $%s.</blockquote>",
                    DynamicEmoji.SUCCESS.getEmoji(), money(data.amount), testAmount.toPlainString()));
            return;
        }
        FcdMarketCsgoMoneySendDto result = send(user, data, remaining);
        if (result == null || result.isOutcomeUnknown()) {
            finish(data, "Пробный перевод подтверждён, результат отправки остатка неизвестен. Не повторяйте перевод до проверки истории Market.CSGO.");
        } else if (!result.isResult()) {
            finish(data, "Пробный перевод подтверждён, но остаток не отправлен. Проверьте доступный баланс и историю Market.CSGO.");
        } else {
            finish(data, String.format("%s <b>Перевод завершён</b>\n<blockquote>Отправлено: <b>$%s</b>, включая пробные $%s.</blockquote>",
                    DynamicEmoji.SUCCESS.getEmoji(), money(data.amount), testAmount.toPlainString()));
        }
    }

    private FcdMarketCsgoMoneySendDto send(UserData user, Data data, BigDecimal amount) {
        FcdMarketCsgoMoneySendInput input = new FcdMarketCsgoMoneySendInput();
        input.setSourceTokenId(data.tokenId);
        input.setDestinationApiKey(data.destinationKey);
        input.setPaymentPassword(data.password);
        input.setCurrency("USD");
        input.setAmount(amount);
        try {
            var answer = endpoint.moneySend(user.getChatId(), input);
            return answer.getStatus() >= 300 ? null : answer.getResponse();
        } catch (RuntimeException error) {
            return null;
        } finally {
            input.setDestinationApiKey(null);
            input.setPaymentPassword(null);
        }
    }

    private void finish(Data data, String result) {
        data.result = result;
        data.stage = Stage.DONE;
        data.eraseSecrets();
    }

    private String money(BigDecimal value) {
        return value.stripTrailingZeros().scale() > 2
                ? value.setScale(3, java.math.RoundingMode.DOWN).toPlainString()
                : value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private Data data(UserData user) {
        return registry.compute(user.getChatId(), (_, previous) -> {
            long now = System.currentTimeMillis();
            if (previous == null) return new Data();
            if (now - previous.lastTouchedAt > FORM_TIMEOUT_MILLIS) {
                previous.eraseSecrets();
                return new Data();
            }
            previous.lastTouchedAt = now;
            return previous;
        });
    }

    @Scheduled(fixedDelay = 60_000)
    public void clearExpiredForms() {
        long now = System.currentTimeMillis();
        registry.forEach((chatId, data) -> {
            if (now - data.lastTouchedAt > FORM_TIMEOUT_MILLIS && registry.remove(chatId, data))
                data.eraseSecrets();
        });
    }

    private enum Stage { SELECT, PASSWORD, DESTINATION, AMOUNT, CONFIRM, PROCESSING, VERIFY, DONE }

    private static class Data {
        private volatile Stage stage = Stage.SELECT;
        private volatile long lastTouchedAt = System.currentTimeMillis();
        private Long tokenId;
        private BigDecimal available;
        private String password;
        private String destinationKey;
        private boolean passwordProvided;
        private boolean destinationProvided;
        private BigDecimal amount;
        private String result;

        private void eraseSecrets() {
            password = null;
            destinationKey = null;
        }
    }
}
