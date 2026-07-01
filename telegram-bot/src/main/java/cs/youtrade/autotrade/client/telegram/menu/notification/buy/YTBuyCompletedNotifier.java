package cs.youtrade.autotrade.client.telegram.menu.notification.buy;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.buy.YTBuyCompletedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class YTBuyCompletedNotifier extends YTTextNotifier<YTBuyCompletedNotification> {
    public YTBuyCompletedNotifier(UserTextMessageSender sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTBuyCompletedNotification data) {
        var priceMap = data.getPriceMap();
        var percentMap = data.getPercentMap();
        var trendMap = data.getTrendMap();

        String priceLine = formatMessageDataMap(priceMap, v -> String.format("<b>$%.2f</b>", v)).trim();
        String profitLine = formatMessageDataMap(percentMap, v -> String.format("<b>%.2f%%</b>", v)).trim();
        String trendLine = formatMessageDataMap(trendMap, v -> String.format("<b>%.2f%%</b>", v)).trim();

        BigDecimal priceFactor = data.getPriceFactor();
        String priceFactorSign = priceFactor.compareTo(BigDecimal.ZERO) >= 0
                ? String.format("%s Дороже на",
                DynamicEmoji.HIGHER.getEmoji())
                : String.format("%s Дешевле на",
                DynamicEmoji.LOWER.getEmoji());

        int unlock = data.getUnlock();
        String availabilityStr = unlock > 0
                ? String.format("Будет доступен через <b>%d дн.</b>", data.getUnlock())
                : "Доступен сейчас";

        return String.format("""
                        %s <b>Покупка завершена</b>
                        
                        <code><b>%s</b></code>
                        <blockquote expandable>%s Цена: <b>$%.2f</b> (%s <b>%.2f%%</b>)
                        %s <b>%s</b>
                        
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
                DynamicEmoji.SUCCESS.getEmoji(),
                data.getItemName(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getPrice(), priceFactorSign, priceFactor,
                DynamicEmoji.BULLET_YELLOW.getEmoji(), availabilityStr,
                // вставляем все тренды
                DynamicEmoji.BULLET_RED.getEmoji(),
                trendLine,
                // вставляем все форматы цен
                DynamicEmoji.BULLET_RED.getEmoji(),
                priceLine,
                // вставляем все форматы процентов
                DynamicEmoji.BULLET_RED.getEmoji(),
                profitLine,
                // Информация
                DynamicEmoji.MAP.getEmoji(), data.getDirection(),
                data.getGivenName(),
                data.getAccountName(), data.getBalance()
        );
    }

    private String formatMessageDataMap(
            Map<String, Double> map,
            Function<Double, String> valueFormatter
    ) {
        return map.entrySet()
                .stream()
                .map(e -> e.getKey() + valueFormatter.apply(e.getValue()))
                .collect(Collectors.joining(" — "));
    }
}
