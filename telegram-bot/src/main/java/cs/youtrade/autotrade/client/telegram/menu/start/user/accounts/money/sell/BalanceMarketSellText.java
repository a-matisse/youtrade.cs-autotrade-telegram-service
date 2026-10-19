package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.BalanceMarketDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BalanceMarketSellText {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM, HH:mm");
    private final int startHour;
    private final int endHour;

    public BalanceMarketSellText(@Value("${yt.balance-market.processing-start-hour}") int startHour,
                                 @Value("${yt.balance-market.processing-end-hour}") int endHour) {
        if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23)
            throw new IllegalArgumentException("Balance Market processing hours must be between 0 and 23");
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public String processingInfo(LocalizedTexts translations) {
        String hours = startHour == 0 && endHour == 0 ? translations.get("around-the-clock")
                : translations.format("daily-from-00-to-00-moscow-time", Map.of("argument1", String.format("%02d", startHour), "argument2", String.format("%02d", endHour)));
        return String.format("""
                └ <i>%s
                %s</i>
                """,
                translations.format("requests-are-processed-automatically-in-the-order", Map.of("argument1", String.valueOf(hours))),
                translations.get("after-creating-a-request-you-can-check")).stripTrailing();
    }



    public String summary(BalanceMarketSellData data, LocalizedTexts translations) {
        return String.format("""
                        %s <i>%s</i>

                        %s <b>%s</b>
                        <blockquote>• %s: %s
                        • %s: %s
                        • %s: %s
                        • %s: %s
                        • %s: %s</blockquote>

                        """,
                        DynamicEmoji.YOUTRADE.getEmoji(),
                        translations.get("sell-market-csgo-balance"),
                        DynamicEmoji.MONEY.getEmoji(),
                        translations.get("request"),
                        translations.get("account"),
                        data.getAccountId() == null ? "—" : "<code>" + data.getAccountId() + "</code>",
                        translations.get("available"),
                        data.getAvailable() == null ? "—" : "<b>$" + money(data.getAvailable()) + "</b>",
                        translations.get("amount"),
                        data.getAmount() == null ? "—" : "<b>$" + money(data.getAmount()) + "</b>",
                        translations.get("receive-as"),
                        data.getMethod() == null ? "—" : "<b>USDT TRC-20</b>",
                        translations.get("address"),
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
