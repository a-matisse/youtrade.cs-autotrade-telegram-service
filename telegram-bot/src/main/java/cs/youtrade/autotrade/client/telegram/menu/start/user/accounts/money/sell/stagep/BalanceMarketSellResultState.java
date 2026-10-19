package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
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
                                        BalanceMarketSellEntry entry, BotTexts botTexts) {
        super(sender, registry, text, botTexts);
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
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellResult");
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        if (data.getResult() == null) return translations.get("could-not-retrieve-the-balance-sale-result");
        return switch (data.getResult()) {
            case SESSION -> session(data.getSession(), translations);
            case ACTIVE_LIST -> active(data.getActive(), translations);
            case UNAVAILABLE -> String.format("""
                    %s <b>%s</b>
                    <blockquote>%s</blockquote>
                    """,
                    DynamicEmoji.WARNING.getEmoji(),
                    translations.get("could-not-load-requests"),
                    translations.get("try-refreshing-the-list-later"));
            case UNKNOWN -> String.format("""
                    %s <b>%s</b>
                    <blockquote>%s</blockquote>
                    """,
                    DynamicEmoji.WARNING.getEmoji(),
                    translations.get("request-creation-outcome-unknown"),
                    translations.get("check-the-request-list-before-trying-again"));
            case REJECTED -> String.format("""
                    %s <b>%s</b>
                    <blockquote>%s</blockquote>
                    """,
                    DynamicEmoji.WARNING.getEmoji(),
                    translations.get("request-not-created"),
                    rejectionText(data, translations));
        };
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellResult");
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        if (data.getResult() != BalanceMarketSellData.Result.ACTIVE_LIST) return supportedState();
        if (!hasText(bot, update, user)) return supportedState();
        synchronized (data) {
            try {
                long id = Long.parseLong(update.getMessage().getText().trim());
                if (data.getActive().stream().noneMatch(session -> session.getSessionId().equals(id))) {
                    sender.sendTextMes(bot, user, translations.get("request-not-found-in-the-list"));
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
                sender.sendTextMes(bot, user, translations.get("enter-a-request-id-as-a-number"));
                return supportedState();
            } catch (RuntimeException ignored) { }
            sender.sendTextMes(bot, user, translations.get("could-not-refresh-the-request-try-again"));
            return supportedState();
        }
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, BalanceMarketSellResultMenu option) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellResult");
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
                    sender.sendTextMes(bot, user, translations.get("could-not-load-requests-try-refreshing-later"));
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
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellResult");
        try {
            var answer = endpoint.get(user.getChatId(), data.getSession().getSessionId());
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.setSession(answer.getResponse().getData());
                return;
            }
        } catch (RuntimeException ignored) { }
        sender.sendTextMes(bot, user, translations.get("could-not-refresh-the-request-try-again"));
    }

    private void cancel(TelegramClient bot, UserData user, BalanceMarketSellData data) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellResult");
        try {
            var answer = endpoint.cancel(user.getChatId(), data.getSession().getSessionId());
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.setSession(answer.getResponse().getData());
                return;
            }
        } catch (RuntimeException ignored) { }
        refreshSession(bot, user, data);
        sender.sendTextMes(bot, user, translations.get("could-not-cancel-the-request-check-its"));
    }

    private String rejectionText(BalanceMarketSellData data, LocalizedTexts translations) {
        String code = data.getFailureCode();
        String message = data.getFailureMessage();
        if ("QUOTE_EXPIRED".equals(code))
            return translations.get("the-quote-has-expired-tap-back-and");
        if ("MARKET_CSGO_ACCOUNT_NOT_CONFIGURED".equals(message))
            return translations.get("the-market-csgo-account-is-not-ready");
        if ("KEY_PROVISIONING_OFFLINE".equals(code)
                || "MANAGING_ACCOUNT_NOT_CONFIGURED".equals(code)
                || "REMAINDER_ACCOUNT_NOT_CONFIGURED".equals(code)
                || "MANAGING_ACCOUNT_NOT_CONFIGURED".equals(message))
            return translations.get("selling-is-not-available-yet-the-service");
        if ("NOT_FOUND".equals(code))
            return translations.get("the-quote-or-account-is-no-longer");
        if ("INVALID_REQUEST".equals(code))
            return translations.get("the-server-rejected-the-request-details-check");
        return translations.get("the-server-rejected-the-request-check-the");
    }

    private String active(List<BalanceMarketDto.Session> sessions, LocalizedTexts translations) {
        if (sessions.isEmpty()) return String.format("""
                %s <i>%s</i>

                %s
                """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                translations.get("sell-market-csgo-balance"),
                translations.get("no-active-requests-tap-back-to-fill"));
        String rows = sessions.stream()
                .map(session -> String.format("• <code>%s</code> — %s",
                        session.getSessionId(),
                        translations.format("account-2", Map.of("value1", String.format("<code>%s</code>", session.getAccountId()), "argument3", String.valueOf(status(session.getStatus(), translations))))))
                .collect(Collectors.joining("\n"));
        return String.format("""
                %s <i>%s</i>

                <blockquote expandable>%s</blockquote>

                %s <b>%s</b>
                """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                translations.get("active-requests"),
                rows,
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("send-a-request-id-to-view-it"));
    }

    private String session(BalanceMarketDto.Session session, LocalizedTexts translations) {
        if (session == null) return translations.get("could-not-load-the-request");
        return String.format("""
                %s <i>%s</i>

                %s <b>%s</b>
                <blockquote>%s</blockquote>

                %s
                """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                translations.get("sell-market-csgo-balance"),
                DynamicEmoji.MONEY.getEmoji(),
                translations.format("request", Map.of("argument3", String.valueOf(session.getSessionId()))),
                sessionDetails(session, translations),
                explanation(session, translations));
    }

    private String sessionDetails(BalanceMarketDto.Session session, LocalizedTexts translations) {
        var rows = new StringJoiner("\n");
        rows.add(String.format("""
                • %s: <b>%s</b>
                • %s: <code>%s</code>
                • %s: <b>$%s</b>
                • %s: <b>USDT TRC-20</b>
                • %s: <code>%s</code>
                """,
                translations.get("status"),
                status(session.getStatus(), translations),
                translations.get("account"),
                session.getAccountId(),
                translations.get("amount"),
                text.money(session.getSourceAmount()),
                translations.get("withdrawal-method"),
                translations.get("address"),
                text.escape(session.getWalletAddress())).stripTrailing());

        String payout = payout(session, translations);
        if (!payout.isEmpty()) rows.add(payout);
        if (session.getRedBalanceOrderId() != null)
            rows.add(String.format("• %s: <code>%s</code>",
                    translations.get("order"),
                    text.escape(session.getRedBalanceOrderId())));
        if (session.getPayoutTxid() != null)
            rows.add(String.format("• TXID: <code>%s</code>", text.escape(session.getPayoutTxid())));
        if (session.getCompletedAt() != null)
            rows.add(String.format("• %s: <b>%s UTC</b>",
                    translations.get("completed-2"),
                    text.date(session.getCompletedAt())));
        return rows.toString();
    }

    private String payout(BalanceMarketDto.Session session, LocalizedTexts translations) {
        boolean paid = "PAID".equals(session.getStatus()) || "COMPLETED".equals(session.getStatus());
        boolean refundReview = "MANUAL_REVIEW".equals(session.getStatus())
                && "REFUND_RESULT_REQUIRES_REVIEW".equals(session.getReason());
        if (paid && session.getActualPayout() != null)
            return String.format("• %s: <b>%s USDT</b>",
                    translations.get("paid-2"),
                    text.money(session.getActualPayout()));
        if (paid || "FAILED".equals(session.getStatus()) || refundReview) return "";
        return String.format("• %s: <b>%s USDT</b>",
                translations.get("to-receive"),
                text.money(session.getActualPayout() == null
                ? session.getEstimatedPayout() : session.getActualPayout()));
    }

    private String explanation(BalanceMarketDto.Session session, LocalizedTexts translations) {
        if ("QUEUED".equals(session.getStatus())) return String.format("""
                %s

                %s
                """, queue(session.getQueueAhead(), translations), text.processingInfo(translations.inBundle("accounts.money-sell.BalanceMarketSellCommon")));
        if ("QUEUE_TIMEOUT".equals(session.getReason()))
            return translations.get("the-request-was-cancelled-automatically-after-more");
        if ("FAILED".equals(session.getStatus())) return failureExplanation(session.getReason(), translations);
        if ("REDBALANCE_SESSION_REFRESH_REQUIRED".equals(session.getReason()))
            return translations.get("the-withdrawal-is-waiting-for-an-operator");
        if ("WAITING_FOR_PAYOUT_APPROVAL".equals(session.getStage()))
            return translations.get("the-payout-is-awaiting-operator-review");
        if ("PAID".equals(session.getStatus()) || "COMPLETED".equals(session.getStatus()))
            return translations.get("the-payout-has-been-confirmed");
        if ("MANUAL_REVIEW".equals(session.getStatus()))
            return "REFUND_RESULT_REQUIRES_REVIEW".equals(session.getReason())
                    ? String.format("""
                            %s
                            %s
                            """,
                            translations.get("the-refund-is-being-checked-credit-to"),
                            translations.get("check-the-status-again-later"))
                    : translations.get("the-request-is-under-manual-review-the");
        if ("PAUSED".equals(session.getStatus()))
            return translations.get("processing-is-paused-you-do-not-need");
        if ("CANCELLED".equals(session.getStatus())) return translations.get("the-request-has-been-cancelled");
        return translations.get("the-request-is-being-processed-check-the");
    }

    private String failureExplanation(String reason, LocalizedTexts translations) {
        return switch (reason) {
            case "REDBALANCE_ACTIVE_ORDER" -> String.format("""
                    %s
                    %s
                    """,
                    translations.get("withdrawals-are-temporarily-unavailable-the-payment-partner"),
                    translations.get("no-funds-were-debited-from-your-account"));
            case "USER_TRANSFER_NOT_RECEIVED" ->
                    translations.get("the-transfer-from-the-source-account-has");
            case "REFUNDED_TO_SOURCE_ACCOUNT" ->
                    translations.get("the-funds-have-been-returned-to-the");
            case "ADMIN_FAILED_MANUAL_REVIEW" ->
                    translations.get("the-request-was-closed-after-manual-review");
            case null, default -> translations.get("contact-support-with-your-request-number");
        };
    }

    private String queue(Integer ahead, LocalizedTexts translations) {
        if (ahead == null) return "";
        return switch (ahead) {
            case 0 -> translations.get("your-request-is-next");
            case 1 -> translations.get("there-is-1-request-ahead-of-yours");
            case 2 -> translations.get("there-are-2-requests-ahead-of-yours");
            case 3 -> translations.get("there-are-3-requests-ahead-of-yours");
            default -> translations.get("there-are-more-than-3-requests-ahead");
        };
    }

    private String status(String value, LocalizedTexts translations) {
        if (value == null) return translations.get("unknown");
        return switch (value) {
            case "QUEUED" -> translations.get("queued");
            case "PROCESSING" -> translations.get("processing");
            case "PAUSED" -> translations.get("paused");
            case "PAID" -> translations.get("paid");
            case "COMPLETED" -> translations.get("completed");
            case "CANCELLED" -> translations.get("cancelled");
            case "FAILED" -> translations.get("failed");
            case "MANUAL_REVIEW" -> translations.get("under-review");
            default -> translations.get("updating");
        };
    }
}
