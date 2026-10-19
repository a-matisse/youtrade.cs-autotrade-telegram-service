package cs.youtrade.autotrade.client.telegram.menu.notification.balance;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.BalanceMarketEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.YTBalanceMarketPayoutNotification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class YTBalanceMarketPayoutNotifier extends YTTextNotifier<YTBalanceMarketPayoutNotification> {
    private static final String KEY_PREFIX = "telegram:balance-market-payout:";
    private static final Duration DEDUP_TTL = Duration.ofDays(90);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM, HH:mm");

    private final RedisTemplate<String, String> redisTemplate;
    private final BalanceMarketEndpoint endpoint;

    public YTBalanceMarketPayoutNotifier(UserTextMessageSender sender, TelegramClient bot,
                                         RedisTemplate<String, String> redisTemplate,
                                         BalanceMarketEndpoint endpoint, BotTexts botTexts) {
        super(sender, bot, botTexts);
        this.redisTemplate = redisTemplate;
        this.endpoint = endpoint;
    }

    @Override
    public void notify(UserData user, YTBalanceMarketPayoutNotification data) {
        if (data.getSessionId() == null)
            throw new IllegalArgumentException("Balance Market payout notification has no sessionId");
        String key = KEY_PREFIX + data.getSessionId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) return;
        var answer = endpoint.get(user.getChatId(), data.getSessionId());
        if (answer.getStatus() >= 300 || answer.getResponse() == null
                || !answer.getResponse().isResult() || answer.getResponse().getData() == null)
            throw new IllegalStateException("Cannot verify Balance Market payout session");
        var session = answer.getResponse().getData();
        if (!"PAID".equals(session.getStatus()) && !"COMPLETED".equals(session.getStatus()))
            throw new IllegalStateException("Balance Market payout session is not paid");
        if (session.getActualPayout() == null || session.getWalletAddress() == null)
            throw new IllegalStateException("Balance Market payout result is incomplete");
        data.setSteamTokenId(session.getAccountId());
        data.setSourceAmount(session.getSourceAmount());
        data.setPayoutAmount(session.getActualPayout());
        data.setWalletAddress(session.getWalletAddress());
        data.setRedBalanceOrderId(session.getRedBalanceOrderId());
        data.setPayoutTxid(session.getPayoutTxid());
        if (!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "sent", DEDUP_TTL))) return;
        try {
            super.notify(user, data);
        } catch (RuntimeException error) {
            redisTemplate.delete(key);
            throw error;
        }
    }

    @Override
    public String getText(UserData user, YTBalanceMarketPayoutNotification data) {
        var translations = botTexts.forUser(user, "notification.BalanceNotifications");
        return String.format("""
                        %s <b>%s</b>

                        <blockquote>• %s: <code>%s</code>
                        • %s: <code>%s</code>
                        • %s: <b>$%s</b>
                        • %s: <b>%s USDT</b>
                        • %s: <b>TRC-20</b>
                        • %s: <code>%s</code>%s%s</blockquote>
                        """,
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.get("balance-sale-completed"),
                        translations.get("request"),
                        data.getSessionId(),
                        translations.get("account"),
                        data.getSteamTokenId(),
                        translations.get("sold"),
                        money(data.getSourceAmount()),
                        translations.get("received"),
                        money(data.getPayoutAmount()),
                        translations.get("network"),
                        translations.get("address"),
                        escape(data.getWalletAddress()),
                        (data.getRedBalanceOrderId() == null ? ""
                        : String.format("""

                                • %s: <code>""",
                                translations.get("order")) + escape(data.getRedBalanceOrderId()) + "</code>")
                + (data.getPayoutTxid() == null ? ""
                        : "\n• TXID: <code>" + escape(data.getPayoutTxid()) + "</code>"),
                        paidAt(data.getPaidAt(), translations));
    }

    private String money(BigDecimal value) {
        return value == null ? "—" : value.stripTrailingZeros().toPlainString();
    }

    private String escape(String value) {
        return value == null ? "—" : value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String paidAt(String value, LocalizedTexts translations) {
        if (value == null) return "";
        try {
            return String.format("""

                    • %s: <b>""",
                    translations.get("paid")) + LocalDateTime.parse(value).format(DATE_FORMAT) + " UTC</b>";
        } catch (RuntimeException error) {
            return "";
        }
    }
}
