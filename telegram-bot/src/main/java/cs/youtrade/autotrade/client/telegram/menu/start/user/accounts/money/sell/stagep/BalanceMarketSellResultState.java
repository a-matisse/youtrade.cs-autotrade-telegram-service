package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.AbstractBalanceMarketSellState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellEntry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellText;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.BalanceMarketDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.BalanceMarketEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class BalanceMarketSellResultState extends AbstractBalanceMarketSellState<BalanceMarketSellResultMenu> {
    private final BalanceMarketEndpoint endpoint;
    private final BalanceMarketSellEntry entry;

    public BalanceMarketSellResultState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                        BalanceMarketSellText text, BalanceMarketEndpoint endpoint,
                                        BalanceMarketSellEntry entry) {
        super(sender, registry, text);
        this.endpoint = endpoint;
        this.entry = entry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_SELL_RESULT;
    }

    @Override
    public Map<BalanceMarketSellResultMenu, Predicate<UserData>> getVisibilityPredicates(UserData user) {
        return Map.of(
                BalanceMarketSellResultMenu.REFRESH, u -> {
                    var data = registry.getOrCreate(u, BalanceMarketSellData::new);
                    return data.getResult() != null && data.getResult() != BalanceMarketSellData.Result.REJECTED;
                },
                BalanceMarketSellResultMenu.CANCEL_SESSION, u -> {
                    var data = registry.getOrCreate(u, BalanceMarketSellData::new);
                    return data.getResult() == BalanceMarketSellData.Result.SESSION
                            && data.getSession() != null && data.getSession().isCanCancel();
                });
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        if (data.getResult() == null) return "Не удалось получить результат продажи баланса.";
        return switch (data.getResult()) {
            case SESSION -> session(data.getSession());
            case ACTIVE_LIST -> active(data.getActive());
            case UNAVAILABLE -> String.format("""
                    %s <b>Не удалось загрузить заявки</b>
                    <blockquote>Попробуйте обновить список позже.</blockquote>
                    """, DynamicEmoji.WARNING.getEmoji());
            case UNKNOWN -> String.format("""
                    %s <b>Результат создания заявки неизвестен</b>
                    <blockquote>Проверьте список заявок перед повторной попыткой.</blockquote>
                    """, DynamicEmoji.WARNING.getEmoji());
            case REJECTED -> String.format("""
                    %s <b>Заявка не создана</b>
                    <blockquote>%s</blockquote>
                    """, DynamicEmoji.WARNING.getEmoji(), rejectionText(data));
        };
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        if (data.getResult() != BalanceMarketSellData.Result.ACTIVE_LIST) return supportedState();
        if (!hasText(bot, update, user)) return supportedState();
        synchronized (data) {
            try {
                long id = Long.parseLong(update.getMessage().getText().trim());
                if (data.getActive().stream().noneMatch(session -> session.getSessionId().equals(id))) {
                    sender.sendTextMes(bot, user, "Заявка не найдена в списке.");
                    return supportedState();
                }
                var answer = endpoint.get(user.getChatId(), id);
                if (answer.getStatus() < 300 && answer.getResponse() != null
                        && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                    data.setSession(answer.getResponse().getData());
                    data.setResult(BalanceMarketSellData.Result.SESSION);
                    return supportedState();
                }
            } catch (NumberFormatException error) {
                sender.sendTextMes(bot, user, "Введите ID заявки числом.");
                return supportedState();
            } catch (RuntimeException ignored) { }
            sender.sendTextMes(bot, user, "Не удалось обновить заявку. Попробуйте позже.");
            return supportedState();
        }
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, BalanceMarketSellResultMenu option) {
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        synchronized (data) {
            if (option == BalanceMarketSellResultMenu.RETURN) return restart(user);
            if (data.getResult() == BalanceMarketSellData.Result.SESSION) {
                if (data.getSession() == null) return supportedState();
                if (option == BalanceMarketSellResultMenu.REFRESH) refreshSession(bot, user, data);
                if (option == BalanceMarketSellResultMenu.CANCEL_SESSION && data.getSession().isCanCancel())
                    cancel(bot, user, data);
            } else if (option == BalanceMarketSellResultMenu.REFRESH
                    && data.getResult() != BalanceMarketSellData.Result.REJECTED) {
                entry.load(user, data);
                if (data.getResult() == BalanceMarketSellData.Result.UNAVAILABLE)
                    sender.sendTextMes(bot, user, "Не удалось загрузить заявки. Попробуйте обновить позже.");
            }
            return supportedState();
        }
    }

    @Override
    public BalanceMarketSellResultMenu getOption(String value) {
        return BalanceMarketSellResultMenu.valueOf(value);
    }

    @Override
    public BalanceMarketSellResultMenu[] getOptions(UserData user) {
        return BalanceMarketSellResultMenu.values();
    }

    private void refreshSession(TelegramClient bot, UserData user, BalanceMarketSellData data) {
        try {
            var answer = endpoint.get(user.getChatId(), data.getSession().getSessionId());
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.setSession(answer.getResponse().getData());
                return;
            }
        } catch (RuntimeException ignored) { }
        sender.sendTextMes(bot, user, "Не удалось обновить заявку. Попробуйте позже.");
    }

    private void cancel(TelegramClient bot, UserData user, BalanceMarketSellData data) {
        try {
            var answer = endpoint.cancel(user.getChatId(), data.getSession().getSessionId());
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.setSession(answer.getResponse().getData());
                return;
            }
        } catch (RuntimeException ignored) { }
        refreshSession(bot, user, data);
        sender.sendTextMes(bot, user, "Не удалось отменить заявку. Проверьте её текущий статус.");
    }

    private String rejectionText(BalanceMarketSellData data) {
        String code = data.getFailureCode();
        String message = data.getFailureMessage();
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

    private String active(List<BalanceMarketDto.Session> sessions) {
        if (sessions.isEmpty()) return String.format("""
                %s <i>Продажа баланса Market.CSGO</i>

                Активных заявок нет. Нажмите «Вернуться», чтобы заполнить заявку заново.
                """, DynamicEmoji.YOUTRADE.getEmoji());
        String rows = sessions.stream()
                .map(session -> String.format("• <code>%s</code> — аккаунт <code>%s</code>, %s",
                        session.getSessionId(), session.getAccountId(), status(session.getStatus())))
                .collect(Collectors.joining("\n"));
        return String.format("""
                %s <i>Активные заявки</i>

                <blockquote expandable>%s</blockquote>

                %s <b>Отправьте ID заявки для просмотра</b>
                """, DynamicEmoji.YOUTRADE.getEmoji(), rows, DynamicEmoji.WRITE.getEmoji());
    }

    private String session(BalanceMarketDto.Session session) {
        if (session == null) return "Не удалось загрузить заявку.";
        return String.format("""
                %s <i>Продажа баланса Market.CSGO</i>

                %s <b>Заявка №%s</b>
                <blockquote>%s</blockquote>

                %s
                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                session.getSessionId(), sessionDetails(session), explanation(session));
    }

    private String sessionDetails(BalanceMarketDto.Session session) {
        var rows = new StringJoiner("\n");
        rows.add(String.format("""
                • Статус: <b>%s</b>
                • Аккаунт: <code>%s</code>
                • Сумма: <b>$%s</b>
                • Способ вывода: <b>USDT TRC-20</b>
                • Адрес: <code>%s</code>
                """, status(session.getStatus()), session.getAccountId(),
                text.money(session.getSourceAmount()), text.escape(session.getWalletAddress())).stripTrailing());

        String payout = payout(session);
        if (!payout.isEmpty()) rows.add(payout);
        if (session.getRedBalanceOrderId() != null)
            rows.add(String.format("• Заказ: <code>%s</code>", text.escape(session.getRedBalanceOrderId())));
        if (session.getPayoutTxid() != null)
            rows.add(String.format("• TXID: <code>%s</code>", text.escape(session.getPayoutTxid())));
        if (session.getCompletedAt() != null)
            rows.add(String.format("• Завершено: <b>%s UTC</b>", text.date(session.getCompletedAt())));
        return rows.toString();
    }

    private String payout(BalanceMarketDto.Session session) {
        boolean paid = "PAID".equals(session.getStatus()) || "COMPLETED".equals(session.getStatus());
        boolean refundReview = "MANUAL_REVIEW".equals(session.getStatus())
                && "REFUND_RESULT_REQUIRES_REVIEW".equals(session.getReason());
        if (paid && session.getActualPayout() != null)
            return String.format("• Выплачено: <b>%s USDT</b>", text.money(session.getActualPayout()));
        if (paid || "FAILED".equals(session.getStatus()) || refundReview) return "";
        return String.format("• К получению: <b>%s USDT</b>", text.money(session.getActualPayout() == null
                ? session.getEstimatedPayout() : session.getActualPayout()));
    }

    private String explanation(BalanceMarketDto.Session session) {
        if ("QUEUED".equals(session.getStatus())) return String.format("""
                %s

                %s
                """, queue(session.getQueueAhead()), text.processingInfo());
        if ("QUEUE_TIMEOUT".equals(session.getReason()))
            return "Заявка автоматически отменена: ожидание в очереди превысило 15 минут.";
        if ("FAILED".equals(session.getStatus())) return failureExplanation(session.getReason());
        if ("REDBALANCE_SESSION_REFRESH_REQUIRED".equals(session.getReason()))
            return "Вывод ожидает обновления авторизации оператором. Повторная заявка не нужна.";
        if ("WAITING_FOR_PAYOUT_APPROVAL".equals(session.getStage()))
            return "Выплата ожидает проверки оператором.";
        if ("PAID".equals(session.getStatus()) || "COMPLETED".equals(session.getStatus()))
            return "Выплата подтверждена.";
        if ("MANUAL_REVIEW".equals(session.getStatus()))
            return "REFUND_RESULT_REQUIRES_REVIEW".equals(session.getReason())
                    ? """
                            Возврат проверяется. Зачисление на аккаунт Market.CSGO пока не подтверждено.
                            Обновите статус позже.
                            """
                    : "Заявка проверяется вручную. Выплата ещё не подтверждена.";
        if ("PAUSED".equals(session.getStatus()))
            return "Выполнение приостановлено; повторно создавать заявку не нужно.";
        if ("CANCELLED".equals(session.getStatus())) return "Заявка отменена.";
        return "Заявка обрабатывается. Обновите статус позже.";
    }

    private String failureExplanation(String reason) {
        return switch (reason) {
            case "REDBALANCE_ACTIVE_ORDER" -> """
                    Вывод временно недоступен: у платёжного партнёра есть незавершённый заказ.
                    Средства с вашего аккаунта не списывались.
                    """;
            case "USER_TRANSFER_NOT_RECEIVED" ->
                    "Перевод с исходного аккаунта не подтверждён. Проверьте его баланс в Market.CSGO.";
            case "REFUNDED_TO_SOURCE_ACCOUNT" ->
                    "Средства возвращены на исходный аккаунт Market.CSGO. Проверьте его баланс.";
            case "ADMIN_FAILED_MANUAL_REVIEW" ->
                    "Заявка закрыта после ручной проверки. За подробностями обратитесь в поддержку, указав номер заявки.";
            case null, default -> "Обратитесь в поддержку, указав номер заявки.";
        };
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
}
