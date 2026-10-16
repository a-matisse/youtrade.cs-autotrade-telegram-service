package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.BalanceMarketDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.BalanceMarketEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class BalanceMarketSellState extends YTPTextMenuState<BalanceMarketSellMenu> {
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("500");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");
    private static final String METHOD = "USDT_TRC20";
    private static final String WALLET_PATTERN = "T[1-9A-HJ-NP-Za-km-z]{33}";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM, HH:mm");

    private final BalanceMarketEndpoint endpoint;
    private final MarketCsgoAccountLookup lookup;
    private final Map<Long, Data> registry = new ConcurrentHashMap<>();

    public BalanceMarketSellState(UserTextMessageSender sender, BalanceMarketEndpoint endpoint,
                                  MarketCsgoAccountLookup lookup) {
        super(sender);
        this.endpoint = endpoint;
        this.lookup = lookup;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL; }

    @Override
    public BalanceMarketSellMenu getOption(String optionStr) {
        return BalanceMarketSellMenu.valueOf(optionStr);
    }

    @Override
    public BalanceMarketSellMenu[] getOptions(UserData user) {
        Data data = data(user);
        return switch (data.stage) {
            case AMOUNT -> new BalanceMarketSellMenu[]{BalanceMarketSellMenu.AMOUNT_500,
                    BalanceMarketSellMenu.AMOUNT_1000, BalanceMarketSellMenu.ALL,
                    BalanceMarketSellMenu.RETURN};
            case METHOD -> new BalanceMarketSellMenu[]{BalanceMarketSellMenu.USDT_TRC20,
                    BalanceMarketSellMenu.RETURN};
            case REVIEW -> new BalanceMarketSellMenu[]{BalanceMarketSellMenu.CONFIRM,
                    BalanceMarketSellMenu.ADDRESS_CHANGE, BalanceMarketSellMenu.RETURN};
            case SESSION -> data.session != null && data.session.isCanCancel()
                    ? new BalanceMarketSellMenu[]{BalanceMarketSellMenu.REFRESH,
                    BalanceMarketSellMenu.CANCEL_SESSION, BalanceMarketSellMenu.RETURN}
                    : new BalanceMarketSellMenu[]{BalanceMarketSellMenu.REFRESH,
                    BalanceMarketSellMenu.RETURN};
            case ACTIVE_LIST, UNAVAILABLE, UNKNOWN -> new BalanceMarketSellMenu[]{
                    BalanceMarketSellMenu.REFRESH, BalanceMarketSellMenu.RETURN};
            default -> new BalanceMarketSellMenu[]{BalanceMarketSellMenu.RETURN};
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        Data data = data(user);
        if (data.stage == Stage.SESSION) return sessionText(data.session);
        if (data.stage == Stage.ACTIVE_LIST) return activeText(data.active);
        if (data.stage == Stage.UNAVAILABLE)
            return DynamicEmoji.WARNING.getEmoji() + " <b>Не удалось загрузить заявки</b>\n"
                    + "<blockquote>Попробуйте обновить список позже.</blockquote>";
        if (data.stage == Stage.UNKNOWN)
            return DynamicEmoji.WARNING.getEmoji() + " <b>Результат создания заявки неизвестен</b>\n"
                    + "<blockquote>Проверьте список заявок перед повторной попыткой.</blockquote>";
        if (data.stage == Stage.REJECTED)
            return DynamicEmoji.WARNING.getEmoji() + " <b>Заявка не создана</b>\n"
                    + "<blockquote>" + data.failure + "</blockquote>";
        String summary = String.format("""
                        %s <i>Продажа баланса Market.CSGO</i>

                        %s <b>Заявка</b>
                        <blockquote>• Аккаунт: %s
                        • Доступно: %s
                        • Сумма: %s
                        • Получение: %s
                        • Адрес: %s</blockquote>

                        """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                data.accountId == null ? "—" : "<code>" + data.accountId + "</code>",
                data.available == null ? "—" : "<b>$" + money(data.available) + "</b>",
                data.amount == null ? "—" : "<b>$" + money(data.amount) + "</b>",
                data.method == null ? "—" : "<b>USDT TRC-20</b>",
                data.wallet == null ? "—" : "<code>" + escape(data.wallet) + "</code>");
        String quote = data.quote == null ? "" : quoteText(data.quote);
        return summary + switch (data.stage) {
            case SELECT -> selectText(user);
            case AMOUNT -> DynamicEmoji.WRITE.getEmoji()
                    + " <b>Выберите сумму или отправьте её сообщением</b>\n"
                    + "└ <i>От $500 до $10 000, не больше доступного баланса. Валюта: USD.</i>";
            case METHOD -> DynamicEmoji.QUESTION.getEmoji()
                    + " <b>Выберите способ получения</b>";
            case WALLET -> DynamicEmoji.WRITE.getEmoji()
                    + " <b>Отправьте полный адрес кошелька USDT TRC-20</b>";
            case PASSWORD -> DynamicEmoji.WRITE.getEmoji()
                    + " <b>Отправьте платёжный пароль Market.CSGO</b>\n"
                    + "└ <i>Сообщение с паролем будет сразу удалено. Затем покажем расчёт для проверки.</i>";
            case REVIEW -> DynamicEmoji.WARNING.getEmoji()
                    + " <b>Проверьте адрес, сеть и расчёт</b>\n"
                    + "<blockquote>• Сеть: <b>TRC-20</b>\n• Адрес: <code>"
                    + escape(data.wallet) + "</code></blockquote>\n\n" + quote
                    + "└ <i>Перевод на неверный адрес или в другой сети может быть необратим. "
                    + "Сумма USDT предварительная; итог зависит от исполнения.</i>";
            case PROCESSING -> DynamicEmoji.WAIT.getEmoji() + " <b>Создаём заявку...</b>";
            default -> "";
        };
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        Data data = data(user);
        synchronized (data) {
            String value = update.getMessage().getText().trim();
            switch (data.stage) {
                case SELECT -> selectAccount(bot, user, data, value);
                case AMOUNT -> {
                    try { chooseAmount(bot, user, data, new BigDecimal(value.replace(',', '.'))); }
                    catch (NumberFormatException error) {
                        sender.sendTextMes(bot, user, "Не удалось распознать сумму. Пример: <code>500.00</code>");
                    }
                }
                case WALLET -> {
                    sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
                    if (!value.matches(WALLET_PATTERN))
                        sender.sendTextMes(bot, user, "Проверьте адрес TRC-20: он должен начинаться с T и содержать 34 символа.");
                    else {
                        data.wallet = value;
                        data.stage = Stage.PASSWORD;
                    }
                }
                case PASSWORD -> {
                    sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
                    if (value.isEmpty()) sender.sendTextMes(bot, user, "Платёжный пароль не может быть пустым.");
                    else {
                        data.password = value;
                        loadQuote(bot, user, data);
                    }
                }
                case ACTIVE_LIST -> {
                    try {
                        long id = Long.parseLong(value);
                        if (data.active.stream().noneMatch(session -> session.getSessionId().equals(id)))
                            sender.sendTextMes(bot, user, "Заявка не найдена в списке.");
                        else refreshSession(bot, user, data, id);
                    } catch (NumberFormatException error) {
                        sender.sendTextMes(bot, user, "Введите ID заявки числом.");
                    }
                }
                default -> { }
            }
        }
        return supportedState();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user,
                                    BalanceMarketSellMenu option) {
        Data data = data(user);
        synchronized (data) {
            if (data.stage == Stage.PROCESSING) return supportedState();
            if (option == BalanceMarketSellMenu.RETURN) {
                if (data.stage == Stage.SESSION || data.stage == Stage.ACTIVE_LIST
                        || data.stage == Stage.UNKNOWN || data.stage == Stage.UNAVAILABLE
                        || data.stage == Stage.REJECTED) {
                    data.eraseSecrets();
                    registry.put(user.getChatId(), new Data());
                    return supportedState();
                }
                return close(user, data);
            }
            if (option == BalanceMarketSellMenu.REFRESH) {
                if (data.stage == Stage.SESSION && data.session != null)
                    refreshSession(bot, user, data, data.session.getSessionId());
                else if (data.stage == Stage.ACTIVE_LIST || data.stage == Stage.UNAVAILABLE
                        || data.stage == Stage.UNKNOWN) loadActive(bot, user, data);
                return supportedState();
            }
            if (data.stage == Stage.AMOUNT) {
                switch (option) {
                    case AMOUNT_500 -> chooseAmount(bot, user, data, MIN_AMOUNT);
                    case AMOUNT_1000 -> chooseAmount(bot, user, data, new BigDecimal("1000"));
                    case ALL -> chooseAmount(bot, user, data, data.available.min(MAX_AMOUNT)
                            .setScale(3, java.math.RoundingMode.DOWN));
                    default -> { }
                }
            } else if (data.stage == Stage.METHOD && option == BalanceMarketSellMenu.USDT_TRC20) {
                data.method = METHOD;
                data.stage = Stage.WALLET;
            } else if (data.stage == Stage.REVIEW) {
                if (option == BalanceMarketSellMenu.ADDRESS_CHANGE) {
                    data.eraseSecrets();
                    data.quote = null;
                    data.wallet = null;
                    data.stage = Stage.WALLET;
                } else if (option == BalanceMarketSellMenu.CONFIRM) {
                    create(bot, user, data);
                }
            } else if (data.stage == Stage.SESSION && option == BalanceMarketSellMenu.CANCEL_SESSION
                    && data.session != null && data.session.isCanCancel()) {
                cancel(bot, user, data);
            }
        }
        return supportedState();
    }

    private UserMenu close(UserData user, Data data) {
        data.eraseSecrets();
        registry.remove(user.getChatId(), data);
        return UserMenu.ACCOUNTS;
    }

    private String selectText(UserData user) {
        try {
            if (!lookup.isMarketCsgo(user.getChatId()))
                return "Продажа баланса доступна только для направления Market.CSGO.";
            return String.format("""
                            %s <b>Отправьте ID аккаунта, с которого хотите продать баланс</b>
                            <blockquote expandable>%s</blockquote>
                            """, DynamicEmoji.WRITE.getEmoji(), lookup.currentPageBalances(user.getChatId()));
        } catch (RuntimeException error) {
            return "Не удалось загрузить аккаунты. Попробуйте снова.";
        }
    }

    private void selectAccount(TelegramClient bot, UserData user, Data data, String value) {
        try {
            if (!lookup.isMarketCsgo(user.getChatId())) {
                sender.sendTextMes(bot, user, "Продажа баланса доступна только для направления Market.CSGO.");
                return;
            }
            long id = Long.parseLong(value);
            var account = lookup.find(user.getChatId(), id);
            if (account == null || account.available() == null) {
                sender.sendTextMes(bot, user, "Аккаунт с продажей Market.CSGO не найден в текущих параметрах.");
                return;
            }
            if (account.available().signum() <= 0) {
                sender.sendTextMes(bot, user, "С этого аккаунта нечего продавать: доступный баланс равен нулю.");
                return;
            }
            data.accountId = id;
            data.available = account.available();
            data.stage = Stage.AMOUNT;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
        }
    }

    private void chooseAmount(TelegramClient bot, UserData user, Data data, BigDecimal amount) {
        if (amount.signum() <= 0 || amount.scale() > 3 || amount.compareTo(MIN_AMOUNT) < 0
                || amount.compareTo(MAX_AMOUNT) > 0) {
            sender.sendTextMes(bot, user, "Сумма должна быть от $500 до $10 000, не более трёх знаков после запятой.");
        } else if (amount.compareTo(data.available) > 0) {
            sender.sendTextMes(bot, user, "Сумма больше доступного баланса: $" + money(data.available) + ".");
        } else {
            data.amount = amount;
            data.stage = Stage.METHOD;
        }
    }

    private void loadQuote(TelegramClient bot, UserData user, Data data) {
        data.quote = null;
        try {
            var answer = endpoint.quote(user.getChatId(),
                    new BalanceMarketDto.QuoteInput(data.accountId, data.amount, data.method));
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && completeQuote(answer.getResponse().getData())) {
                data.quote = answer.getResponse().getData();
                data.stage = Stage.REVIEW;
                return;
            }
        } catch (RuntimeException ignored) { }
        data.eraseSecrets();
        data.stage = Stage.PASSWORD;
        sender.sendTextMes(bot, user, "Котировка сейчас недоступна. Проверьте баланс и повторите запрос позже.");
    }

    private void create(TelegramClient bot, UserData user, Data data) {
        if (data.password == null || data.quote == null || expired(data.quote)) {
            data.eraseSecrets();
            data.quote = null;
            data.stage = Stage.PASSWORD;
            sender.sendTextMes(bot, user, "Котировка истекла. Введите пароль ещё раз, чтобы проверить новый расчёт.");
            return;
        }
        data.stage = Stage.PROCESSING;
        BalanceMarketDto.CreateInput input = new BalanceMarketDto.CreateInput();
        input.setQuoteId(data.quote.getQuoteId());
        input.setAccountId(data.accountId);
        input.setAmount(data.quote.getSourceAmount());
        input.setWithdrawalMethod(data.method);
        input.setWalletAddress(data.wallet);
        input.setPaymentPassword(data.password);
        input.setWalletConfirmed(true);
        try {
            var answer = endpoint.create(user.getChatId(), UUID.randomUUID().toString(), input);
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.session = answer.getResponse().getData();
                data.stage = Stage.SESSION;
                data.quote = null;
                return;
            }
            if (answer.getStatus() >= 400 && answer.getStatus() < 500 && answer.getStatus() != 408) {
                var response = answer.getResponse();
                data.failure = rejectionText(response);
                data.stage = Stage.REJECTED;
                data.quote = null;
                log.warn("Balance Market session creation rejected: HTTP {}, code {}, reason {}",
                        answer.getStatus(), response == null ? null : response.getCode(), rejectionReason(response));
                return;
            }
            log.warn("Balance Market session creation outcome unknown: HTTP {}", answer.getStatus());
        } catch (RuntimeException error) {
            log.warn("Balance Market session creation outcome unknown: {}", error.getClass().getSimpleName());
        } finally {
            input.setPaymentPassword(null);
            data.eraseSecrets();
        }
        data.quote = null;
        data.stage = Stage.UNKNOWN;
        sender.sendTextMes(bot, user, "Не удалось подтвердить создание заявки. Проверьте список заявок перед повтором.");
    }

    private String rejectionText(BalanceMarketDto.SessionAnswer response) {
        String code = response == null ? null : response.getCode();
        String message = response == null ? null : response.getMessage();
        if ("QUOTE_EXPIRED".equals(code))
            return "Котировка истекла. Нажмите «Вернуться» и заполните заявку заново.";
        if ("MARKET_CSGO_ACCOUNT_NOT_CONFIGURED".equals(message))
            return "Аккаунт Market.CSGO не готов к продаже баланса. Проверьте его подключение.";
        if ("KEY_PROVISIONING_OFFLINE".equals(code)
                || "MANAGING_ACCOUNT_NOT_CONFIGURED".equals(code)
                || "REMAINDER_ACCOUNT_NOT_CONFIGURED".equals(code)
                || "MANAGING_ACCOUNT_NOT_CONFIGURED".equals(message))
            return "Продажа пока недоступна: сервис ещё не готов к обработке заявки.";
        if ("NOT_FOUND".equals(code))
            return "Котировка или аккаунт больше не доступны. Нажмите «Вернуться» и заполните заявку заново.";
        if ("INVALID_REQUEST".equals(code))
            return "Сервер отклонил данные заявки. Проверьте сумму, сеть и адрес кошелька.";
        return "Сервер отклонил заявку. Проверьте данные и попробуйте позже.";
    }

    private String rejectionReason(BalanceMarketDto.SessionAnswer response) {
        if (response == null) return "UNKNOWN";
        String message = response.getMessage();
        if ("MARKET_CSGO_ACCOUNT_NOT_CONFIGURED".equals(message)
                || "MANAGING_ACCOUNT_NOT_CONFIGURED".equals(message)
                || "REMAINDER_ACCOUNT_NOT_CONFIGURED".equals(message)
                || "Idempotency-Key belongs to a different request".equals(message))
            return message;
        return "UNKNOWN";
    }

    private void loadActive(TelegramClient bot, UserData user, Data data) {
        try {
            var answer = endpoint.active(user.getChatId());
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.active = answer.getResponse().getData();
                data.session = data.active.size() == 1 ? data.active.getFirst() : null;
                data.stage = data.session == null ? Stage.ACTIVE_LIST : Stage.SESSION;
                return;
            }
        } catch (RuntimeException ignored) { }
        data.stage = Stage.UNAVAILABLE;
        sender.sendTextMes(bot, user, "Не удалось загрузить заявки. Попробуйте обновить позже.");
    }

    private void refreshSession(TelegramClient bot, UserData user, Data data, long sessionId) {
        try {
            var answer = endpoint.get(user.getChatId(), sessionId);
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.session = answer.getResponse().getData();
                data.stage = Stage.SESSION;
                return;
            }
        } catch (RuntimeException ignored) { }
        sender.sendTextMes(bot, user, "Не удалось обновить заявку. Попробуйте позже.");
    }

    private void cancel(TelegramClient bot, UserData user, Data data) {
        try {
            var answer = endpoint.cancel(user.getChatId(), data.session.getSessionId());
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.session = answer.getResponse().getData();
                return;
            }
        } catch (RuntimeException ignored) { }
        refreshSession(bot, user, data, data.session.getSessionId());
        sender.sendTextMes(bot, user, "Не удалось отменить заявку. Проверьте её текущий статус.");
    }

    private String quoteText(BalanceMarketDto.Quote quote) {
        return String.format("""
                        %s <b>Котировка</b>
                        <blockquote>• К списанию: <b>$%s</b>
                        • Общая ставка: <b>%s%%</b>
                        • Комиссия Y.CS: <b>$%s</b>
                        • Комиссия сети: <b>$%s</b>
                        • К получению: <b>%s USDT</b>
                        • Курс обновлён: <b>%s UTC</b>
                        • Действует до: <b>%s UTC</b></blockquote>

                        """, DynamicEmoji.GRAPH.getEmoji(),
                money(quote.getSourceAmount()), money(quote.getTotalFeePercent()),
                money(quote.getTotalFee()), money(quote.getNetworkFee()), money(quote.getEstimatedPayout()),
                date(quote.getRateUpdatedAt()), date(quote.getExpiresAt()));
    }

    private boolean completeQuote(BalanceMarketDto.Quote quote) {
        return quote != null && quote.getQuoteId() != null && quote.getSourceAmount() != null
                && quote.getTotalFeePercent() != null && quote.getTotalFee() != null
                && quote.getNetworkFee() != null && quote.getEstimatedPayout() != null
                && quote.getRateUpdatedAt() != null && quote.getExpiresAt() != null;
    }

    private String activeText(List<BalanceMarketDto.Session> sessions) {
        if (sessions.isEmpty()) return DynamicEmoji.YOUTRADE.getEmoji()
                + " <i>Продажа баланса Market.CSGO</i>\n\n"
                + "Активных заявок нет. Нажмите «Вернуться», чтобы заполнить заявку заново.";
        StringBuilder text = new StringBuilder(DynamicEmoji.YOUTRADE.getEmoji()
                + " <i>Активные заявки</i>\n\n<blockquote expandable>");
        for (var session : sessions) text.append("• <code>").append(session.getSessionId())
                .append("</code> — аккаунт <code>").append(session.getAccountId())
                .append("</code>, ").append(status(session.getStatus())).append("\n");
        return text.append("</blockquote>\n\n" + DynamicEmoji.WRITE.getEmoji()
                + " <b>Отправьте ID заявки для просмотра</b>").toString();
    }

    private String sessionText(BalanceMarketDto.Session session) {
        if (session == null) return "Не удалось загрузить заявку.";
        String actual = session.getActualPayout() == null ? "" : "\n• Выплачено: <b>"
                + money(session.getActualPayout()) + " USDT</b>";
        String order = session.getRedBalanceOrderId() == null ? "" : "\n• Заказ: <code>"
                + escape(session.getRedBalanceOrderId()) + "</code>";
        String txid = session.getPayoutTxid() == null ? "" : "\n• TXID: <code>"
                + escape(session.getPayoutTxid()) + "</code>";
        String completed = session.getCompletedAt() == null ? "" : "\n• Завершено: <b>"
                + date(session.getCompletedAt()) + " UTC</b>";
        String payout = "FAILED".equals(session.getStatus()) ? "" : "\n• К получению: <b>"
                + money(session.getEstimatedPayout()) + " USDT</b>";
        return String.format("""
                        %s <i>Продажа баланса Market.CSGO</i>

                        %s <b>Заявка №%s</b>
                        <blockquote>• Статус: <b>%s</b>
                        • Аккаунт: <code>%s</code>
                        • Списываем: <b>$%s</b>
                        • Способ вывода: <b>USDT TRC-20</b>
                        • Адрес: <code>%s</code>%s%s%s%s%s</blockquote>

                        %s
                        """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                session.getSessionId(), status(session.getStatus()), session.getAccountId(),
                money(session.getSourceAmount()), escape(session.getWalletAddress()),
                payout, actual, order, txid, completed,
                explanation(session));
    }

    private String explanation(BalanceMarketDto.Session session) {
        if ("QUEUED".equals(session.getStatus())) return queue(session.getQueueAhead());
        if ("QUEUE_TIMEOUT".equals(session.getReason()))
            return "Заявка автоматически отменена: ожидание в очереди превысило 15 минут.";
        if ("FAILED".equals(session.getStatus())) {
            if ("USER_TRANSFER_NOT_RECEIVED".equals(session.getReason()))
                return "Перевод с исходного аккаунта не подтверждён. Проверьте его баланс в Market.CSGO.";
            if ("REFUNDED_TO_SOURCE_ACCOUNT".equals(session.getReason()))
                return "Средства возвращены на исходный аккаунт Market.CSGO. Проверьте его баланс.";
            if ("ADMIN_FAILED_MANUAL_REVIEW".equals(session.getReason()))
                return "Заявка закрыта после ручной проверки. За подробностями обратитесь в поддержку, указав номер заявки.";
            return "Обратитесь в поддержку, указав номер заявки.";
        }
        if ("REDBALANCE_SESSION_REFRESH_REQUIRED".equals(session.getReason()))
            return "Вывод ожидает обновления авторизации оператором. Повторная заявка не нужна.";
        if ("WAITING_FOR_PAYOUT_APPROVAL".equals(session.getStage()))
            return "Выплата ожидает проверки оператором.";
        if ("PAID".equals(session.getStatus()) || "COMPLETED".equals(session.getStatus()))
            return "Выплата подтверждена.";
        if ("MANUAL_REVIEW".equals(session.getStatus()))
            return "Заявка проверяется вручную. Выплата ещё не подтверждена.";
        if ("PAUSED".equals(session.getStatus()))
            return "Выполнение приостановлено; повторно создавать заявку не нужно.";
        if ("CANCELLED".equals(session.getStatus())) return "Заявка отменена.";
        return "Заявка обрабатывается. Обновите статус позже.";
    }

    private String queue(Integer ahead) {
        if (ahead == null) return "";
        return switch (ahead) {
            case 0 -> "Ваша заявка следующая.";
            case 1 -> "Перед вами 1 заявка.";
            case 2 -> "Перед вами 2 заявки.";
            case 3 -> "Перед вами 3 заявки.";
            default -> "Перед вами больше 3 заявок.";
        };
    }

    private String status(String value) {
        if (value == null) return "неизвестен";
        return switch (value) {
            case "QUEUED" -> "в очереди";
            case "PROCESSING" -> "в работе";
            case "PAUSED" -> "приостановлена";
            case "PAID" -> "выплачено";
            case "COMPLETED" -> "завершена";
            case "CANCELLED" -> "отменена";
            case "FAILED" -> "не выполнена";
            case "MANUAL_REVIEW" -> "на проверке";
            default -> "обновляется";
        };
    }

    private boolean expired(BalanceMarketDto.Quote quote) {
        return quote.getExpiresAt() == null
                || !quote.getExpiresAt().isAfter(LocalDateTime.now(ZoneOffset.UTC));
    }

    private String money(BigDecimal value) {
        return value == null ? "—" : value.stripTrailingZeros().toPlainString();
    }

    private String date(LocalDateTime value) {
        return value == null ? "—" : value.format(DATE_FORMAT);
    }

    private String escape(String value) {
        return value == null ? "—" : value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private Data data(UserData user) {
        return registry.computeIfAbsent(user.getChatId(), _ -> {
            Data data = new Data();
            try {
                var answer = endpoint.active(user.getChatId());
                if (answer.getStatus() < 300 && answer.getResponse() != null
                        && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                    data.active = answer.getResponse().getData();
                    if (!data.active.isEmpty()) {
                        data.session = data.active.size() == 1 ? data.active.getFirst() : null;
                        data.stage = data.session == null ? Stage.ACTIVE_LIST : Stage.SESSION;
                    }
                } else data.stage = Stage.UNAVAILABLE;
            } catch (RuntimeException error) {
                data.stage = Stage.UNAVAILABLE;
            }
            return data;
        });
    }

    @Scheduled(fixedDelay = 10_000)
    public void clearExpiredPasswords() {
        for (Data data : registry.values()) {
            synchronized (data) {
                if (data.password != null && (data.quote == null || expired(data.quote))) {
                    data.eraseSecrets();
                    data.quote = null;
                    if (data.stage == Stage.REVIEW) data.stage = Stage.PASSWORD;
                }
            }
        }
    }

    private enum Stage {
        SELECT, AMOUNT, METHOD, WALLET, PASSWORD, REVIEW,
        PROCESSING, SESSION, ACTIVE_LIST, UNAVAILABLE, UNKNOWN, REJECTED
    }

    private static class Data {
        private volatile Stage stage = Stage.SELECT;
        private Long accountId;
        private BigDecimal available;
        private BigDecimal amount;
        private String method;
        private String wallet;
        private String password;
        private BalanceMarketDto.Quote quote;
        private BalanceMarketDto.Session session;
        private List<BalanceMarketDto.Session> active = List.of();
        private String failure;

        private void eraseSecrets() {
            password = null;
        }
    }
}
