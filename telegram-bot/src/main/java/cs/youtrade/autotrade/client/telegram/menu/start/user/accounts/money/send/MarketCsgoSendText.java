package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MarketCsgoSendText {
    public static String summary(MarketCsgoSendData data, LocalizedTexts translations) {
        return String.format("""
                        %s <i>%s</i>

                        %s <b>%s</b>
                        <blockquote>• %s: %s
                        • %s: %s
                        • %s: %s
                        • %s: %s
                        • %s: <b>USD</b>
                        • %s: %s</blockquote>

                        """,
                        DynamicEmoji.YOUTRADE.getEmoji(),
                        translations.get("withdraw-from-market-csgo"),
                        DynamicEmoji.MONEY.getEmoji(),
                        translations.get("transfer"),
                        translations.get("account"),
                        data.getTokenId() == null ? "—" : "<code>" + data.getTokenId() + "</code>",
                        translations.get("available-before-transfer"),
                        data.getAvailable() == null ? "—" : "<b>$" + money(data.getAvailable()) + "</b>",
                        translations.get("payment-password"),
                        data.isPasswordProvided() ? String.format("✅ %s",
                        translations.get("received")) : "—",
                        translations.get("recipient"),
                        data.isDestinationProvided() ? String.format("✅ %s",
                        translations.get("key-provided")) : "—",
                        translations.get("transfer-currency"),
                        translations.get("amount"),
                        data.getAmount() == null ? "—" : "<b>$" + money(data.getAmount()) + "</b>");
    }

    public static String money(BigDecimal value) {
        return value.stripTrailingZeros().scale() > 2
                ? value.setScale(3, RoundingMode.DOWN).toPlainString()
                : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
