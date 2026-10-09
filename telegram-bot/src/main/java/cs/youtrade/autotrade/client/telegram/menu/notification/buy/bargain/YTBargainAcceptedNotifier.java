package cs.youtrade.autotrade.client.telegram.menu.notification.buy.bargain;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.buy.YTBargainNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class YTBargainAcceptedNotifier extends YTTextNotifier<YTBargainNotification> {
    public YTBargainAcceptedNotifier(
            UserTextMessageSender sender,
            TelegramClient bot
    ) {
        super(sender, bot);
    }

    @Override
    public String getText(YTBargainNotification data) {
        String priceFactor = "";
        if (data.getPriceFactor() != null) {
            String sign = data.getPriceFactor().compareTo(BigDecimal.ZERO) >= 0
                    ? DynamicEmoji.HIGHER.getEmoji() + " Дороже на"
                    : DynamicEmoji.LOWER.getEmoji() + " Дешевле на";
            priceFactor = String.format(" (%s <b>%.2f%%</b>)", sign, data.getPriceFactor());
        }

        StringBuilder details = new StringBuilder(String.format("%s Цена: <b>$%.2f</b>%s\n%s Скидка: <b>%.2f%%</b>\n",
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getPrice(), priceFactor,
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getAdditionalProfit()));
        if (data.getUnlock() != null) {
            String availability = data.getUnlock() > 0
                    ? String.format("Будет доступен через <b>%d дн.</b>", data.getUnlock())
                    : "Доступен сейчас";
            details.append(String.format("%s <b>%s</b>\n",
                    DynamicEmoji.BULLET_YELLOW.getEmoji(), availability));
        }
        if (data.getPriceMap() != null && data.getPercentMap() != null && data.getTrendMap() != null) {
            String trendLine = formatMessageDataMap(data.getTrendMap(), value -> String.format("<b>%.2f%%</b>", value));
            String priceLine = formatMessageDataMap(data.getPriceMap(), value -> String.format("<b>$%.2f</b>", value));
            String profitLine = formatMessageDataMap(data.getPercentMap(), value -> String.format("<b>%.2f%%</b>", value));
            details.append(String.format("""

                    %s <b>Тренд</b>
                    %s

                    %s <b>Продажа</b>
                    %s

                    %s <b>Наценка</b>
                    %s
                    """,
                    DynamicEmoji.BULLET_RED.getEmoji(), trendLine,
                    DynamicEmoji.BULLET_RED.getEmoji(), priceLine,
                    DynamicEmoji.BULLET_RED.getEmoji(), profitLine));
        }
        String balance = data.getBalance() == null
                ? ""
                : String.format(" [<i>$</i><b>%.2f</b>]", data.getBalance());

        return String.format("""
                        %s <b>Торг исполнен успешно</b>

                        <code><b>%s</b></code>
                        <blockquote expandable>%s</blockquote>

                        %s %s
                        <blockquote>• Параметры: <b>%s</b>
                        • Аккаунт: <b>%s</b>%s</blockquote>

                        ⚠️ <b>Внимание!</b> Занижение цены продажи снизит вашу прибыль
                        """,
                DynamicEmoji.ON.getEmoji(), data.getItemName(), details.toString().trim(),
                DynamicEmoji.MAP.getEmoji(), data.getDirection(),
                data.getGivenName(), data.getAccountName(), balance
        );
    }

    private String formatMessageDataMap(Map<String, Double> map, Function<Double, String> formatter) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + formatter.apply(entry.getValue()))
                .collect(Collectors.joining(" — "));
    }
}
