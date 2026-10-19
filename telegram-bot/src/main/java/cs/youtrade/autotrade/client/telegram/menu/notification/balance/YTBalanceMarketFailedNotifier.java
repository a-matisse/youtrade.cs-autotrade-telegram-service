package cs.youtrade.autotrade.client.telegram.menu.notification.balance;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.BalanceMarketEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.YTBalanceMarketFailedNotification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Duration;

@Component
public class YTBalanceMarketFailedNotifier extends YTTextNotifier<YTBalanceMarketFailedNotification> {
    private final RedisTemplate<String, String> redisTemplate;
    private final BalanceMarketEndpoint endpoint;

    public YTBalanceMarketFailedNotifier(UserTextMessageSender sender, TelegramClient bot,
                                         RedisTemplate<String, String> redisTemplate,
                                         BalanceMarketEndpoint endpoint, BotTexts botTexts) {
        super(sender, bot, botTexts);
        this.redisTemplate = redisTemplate;
        this.endpoint = endpoint;
    }

    @Override
    public void notify(UserData user, YTBalanceMarketFailedNotification data) {
        if (data.getSessionId() == null)
            throw new IllegalArgumentException("Balance Market failure notification has no sessionId");
        var answer = endpoint.get(user.getChatId(), data.getSessionId());
        if (answer.getStatus() >= 300 || answer.getResponse() == null
                || !answer.getResponse().isResult() || answer.getResponse().getData() == null)
            throw new IllegalStateException("Cannot verify Balance Market failed session");
        var session = answer.getResponse().getData();
        if (!"FAILED".equals(session.getStatus()))
            throw new IllegalStateException("Balance Market session is not failed");
        data.setSteamTokenId(session.getAccountId());
        data.setSourceAmount(session.getSourceAmount());
        data.setReason(session.getReason());
        String key = "telegram:balance-market-failed:" + data.getSessionId();
        if (!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "sent", Duration.ofDays(90)))) return;
        try {
            super.notify(user, data);
        } catch (RuntimeException error) {
            redisTemplate.delete(key);
            throw error;
        }
    }

    @Override
    public String getText(UserData user, YTBalanceMarketFailedNotification data) {
        var translations = botTexts.forUser(user, "notification.BalanceNotifications");
        String reason = switch (data.getReason() == null ? "" : data.getReason()) {
            case "REDBALANCE_ACTIVE_ORDER" ->
                    String.format("""
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
            default -> translations.get("contact-support-with-your-request-number");
        };
        return String.format("""
                        %s <b>%s</b>

                        <blockquote>• %s: <code>%s</code>
                        • %s: <code>%s</code>
                        • %s: <b>$%s</b></blockquote>

                        %s
                        """,
                        DynamicEmoji.ERROR.getEmoji(),
                        translations.get("balance-sale-failed"),
                        translations.get("request"),
                        data.getSessionId(),
                        translations.get("account"),
                        data.getSteamTokenId(),
                        translations.get("to-be-debited"),
                        data.getSourceAmount() == null ? "—" : data.getSourceAmount().stripTrailingZeros().toPlainString(),
                        reason);
    }
}
