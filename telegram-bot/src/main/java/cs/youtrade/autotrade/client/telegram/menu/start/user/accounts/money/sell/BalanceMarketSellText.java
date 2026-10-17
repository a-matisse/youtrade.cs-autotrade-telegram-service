package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.BalanceMarketDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BalanceMarketSellText {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM, HH:mm");
    private final String processingInfo;

    public BalanceMarketSellText(@Value("${yt.balance-market.processing-start-hour}") int startHour,
                                 @Value("${yt.balance-market.processing-end-hour}") int endHour) {
        if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23)
            throw new IllegalArgumentException("Balance Market processing hours must be between 0 and 23");
        String hours = startHour == 0 && endHour == 0 ? "круглосуточно"
                : String.format("ежедневно с %02d:00 до %02d:00 МСК", startHour, endHour);
        processingInfo = String.format("""
                └ <i>Заявки обрабатываются автоматически %s в порядке поступления.
                Очередь можно отслеживать кнопкой «Обновить» после создания заявки.</i>
                """, hours).stripTrailing();
    }

    public String processingInfo() { return processingInfo; }

    public String summary(BalanceMarketSellData data) {
        return String.format("""
                        %s <i>Продажа баланса Market.CSGO</i>

                        %s <b>Заявка</b>
                        <blockquote>• Аккаунт: %s
                        • Доступно: %s
                        • Сумма: %s
                        • Получение: %s
                        • Адрес: %s</blockquote>

                        """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                data.getAccountId() == null ? "—" : "<code>" + data.getAccountId() + "</code>",
                data.getAvailable() == null ? "—" : "<b>$" + money(data.getAvailable()) + "</b>",
                data.getAmount() == null ? "—" : "<b>$" + money(data.getAmount()) + "</b>",
                data.getMethod() == null ? "—" : "<b>USDT TRC-20</b>",
                data.getWallet() == null ? "—" : "<code>" + escape(data.getWallet()) + "</code>");
    }

    public boolean completeQuote(BalanceMarketDto.Quote quote) {
        return quote != null && quote.getQuoteId() != null && quote.getSourceAmount() != null
                && quote.getTotalFeePercent() != null && quote.getTotalFee() != null
                && quote.getNetworkFee() != null && quote.getEstimatedPayout() != null
                && quote.getRateUpdatedAt() != null && quote.getExpiresAt() != null;
    }

    public String money(BigDecimal value) {
        return value == null ? "—" : value.stripTrailingZeros().toPlainString();
    }

    public String date(LocalDateTime value) {
        return value == null ? "—" : value.format(DATE_FORMAT);
    }

    public String escape(String value) {
        return value == null ? "—" : value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
