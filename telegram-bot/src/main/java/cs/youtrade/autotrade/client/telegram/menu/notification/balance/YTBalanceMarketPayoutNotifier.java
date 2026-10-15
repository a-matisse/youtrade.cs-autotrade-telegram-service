package cs.youtrade.autotrade.client.telegram.menu.notification.balance;

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
                                         BalanceMarketEndpoint endpoint) {
        super(sender, bot);
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
    public String getText(YTBalanceMarketPayoutNotification data) {
        return String.format("""
                        %s <b>Продажа баланса завершена</b>

                        <blockquote>• Заявка: <code>%s</code>
                        • Аккаунт: <code>%s</code>
                        • Продано: <b>$%s</b>
                        • Получено: <b>%s USDT</b>
                        • Сеть: <b>TRC-20</b>
                        • Адрес: <code>%s</code>%s%s</blockquote>
                        """, DynamicEmoji.SUCCESS.getEmoji(), data.getSessionId(), data.getSteamTokenId(),
                money(data.getSourceAmount()), money(data.getPayoutAmount()),
                escape(data.getWalletAddress()),
                (data.getRedBalanceOrderId() == null ? ""
                        : "\n• Заказ: <code>" + escape(data.getRedBalanceOrderId()) + "</code>")
                + (data.getPayoutTxid() == null ? ""
                        : "\n• TXID: <code>" + escape(data.getPayoutTxid()) + "</code>"),
                paidAt(data.getPaidAt()));
    }

    private String money(BigDecimal value) {
        return value == null ? "—" : value.stripTrailingZeros().toPlainString();
    }

    private String escape(String value) {
        return value == null ? "—" : value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String paidAt(String value) {
        if (value == null) return "";
        try {
            return "\n• Выплачено: <b>" + LocalDateTime.parse(value).format(DATE_FORMAT) + " UTC</b>";
        } catch (RuntimeException error) {
            return "";
        }
    }
}
