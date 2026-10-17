package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send;

import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MarketCsgoSendText {
    public static String summary(MarketCsgoSendData data) {
        return String.format("""
                        %s <i>Вывод с Market.CSGO</i>

                        %s <b>Перевод</b>
                        <blockquote>• Аккаунт: %s
                        • Доступно до перевода: %s
                        • Платёжный пароль: %s
                        • Получатель: %s
                        • Валюта перевода: <b>USD</b>
                        • Сумма: %s</blockquote>

                        """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                data.getTokenId() == null ? "—" : "<code>" + data.getTokenId() + "</code>",
                data.getAvailable() == null ? "—" : "<b>$" + money(data.getAvailable()) + "</b>",
                data.isPasswordProvided() ? "✅ Получен" : "—",
                data.isDestinationProvided() ? "✅ Ключ указан" : "—",
                data.getAmount() == null ? "—" : "<b>$" + money(data.getAmount()) + "</b>");
    }

    public static String money(BigDecimal value) {
        return value.stripTrailingZeros().scale() > 2
                ? value.setScale(3, RoundingMode.DOWN).toPlainString()
                : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
