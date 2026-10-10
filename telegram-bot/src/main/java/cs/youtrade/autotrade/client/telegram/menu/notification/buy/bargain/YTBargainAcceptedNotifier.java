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

        String details = String.format("%s Цена: <b>$%.2f</b>%s\n%s Скидка: <b>%.2f%%</b>",
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getPrice(), priceFactor,
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getAdditionalProfit());
        String trendLine = formatMessageDataMap(data.getTrendMap(), value ->
                String.format("<b>%.2f%%</b>", value));
        String priceLine = formatMessageDataMap(data.getPriceMap(), value ->
                String.format("<b>$%.2f</b>", value));
        String profitLine = formatMessageDataMap(data.getPercentMap(), value ->
                String.format("<b>%.2f%%</b>", value));

        return String.format("""
                        %s <b>Торг исполнен успешно</b>
                        
                        <code><b>%s</b></code>
                        <blockquote expandable>%s
                        
                        %s <b>Тренд</b>
                        %s
                        
                        %s <b>Продажа</b>
                        %s
                        
                        %s <b>Наценка</b>
                        %s</blockquote>
                        
                        %s %s
                        <blockquote>• Параметры: <b>%s</b>
                        • Аккаунт: <b>%s</b> [<i>$</i><b>%.2f</b>]</blockquote>
                        
                        ⚠️ <b>Внимание!</b> Занижение цены продажи снизит вашу прибыль
                        """,
                DynamicEmoji.ON.getEmoji(),
                data.getItemName(),
                details,
                // вставляем все тренды
                DynamicEmoji.BULLET_RED.getEmoji(), trendLine,
                // вставляем все форматы цен
                DynamicEmoji.BULLET_RED.getEmoji(), priceLine,
                // вставляем все форматы процентов
                DynamicEmoji.BULLET_RED.getEmoji(), profitLine,
                // Информация
                DynamicEmoji.MAP.getEmoji(), data.getDirection(),
                data.getGivenName(),
                data.getAccountName(), data.getBalance()
        );
    }

    private String formatMessageDataMap(Map<String, Double> map, Function<Double, String> formatter) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + formatter.apply(entry.getValue()))
                .collect(Collectors.joining(" — "));
    }
}
