package cs.youtrade.autotrade.client.telegram.menu.notification.buy;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

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
    public YTBuyCompletedNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTBuyCompletedNotification data) {
        var translations = botTexts.forUser(user, "notification.BuyNotifications");
        var priceMap = data.getPriceMap();
        var percentMap = data.getPercentMap();
        var trendMap = data.getTrendMap();

        String priceLine = formatMessageDataMap(priceMap, v -> String.format("<b>$%.2f</b>", v)).trim();
        String profitLine = formatMessageDataMap(percentMap, v -> String.format("<b>%.2f%%</b>", v)).trim();
        String trendLine = formatMessageDataMap(trendMap, v -> String.format("<b>%.2f%%</b>", v)).trim();

        BigDecimal priceFactor = data.getPriceFactor();
        String priceFactorSign = priceFactor.compareTo(BigDecimal.ZERO) >= 0
                ? String.format("%s %s",
                        DynamicEmoji.HIGHER.getEmoji(),
                        translations.get("more-expensive-by"))
                : String.format("%s %s",
                        DynamicEmoji.LOWER.getEmoji(),
                        translations.get("cheaper-by"));

        int unlock = data.getUnlock();
        String availabilityStr = unlock > 0
                ? translations.format("available-in", Map.of("value1", String.format("<b>%s</b>", translations.format("unlock-days", Map.of("days", unlock)))))
                : translations.get("available-now");

        return String.format("""
                        %s <b>%s</b>
                        
                        <code><b>%s</b></code>
                        <blockquote expandable>%s %s: <b>$%.2f</b> (%s <b>%.2f%%</b>)
                        %s <b>%s</b>
                        
                        %s <b>%s</b>
                        %s
                        
                        %s <b>%s</b>
                        %s
                        
                        %s <b>%s</b>
                        %s</blockquote>
                        
                        %s %s
                        <blockquote>• %s: <b>%s</b>
                        • %s: <b>%s</b> [<i>$</i><b>%.2f</b>]</blockquote>
                        
                        ⚠️ %s
                        """,
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.get("purchase-completed"),
                        data.getItemName(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("price"),
                        data.getPrice(),
                        priceFactorSign,
                        priceFactor,
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        availabilityStr,
                        DynamicEmoji.BULLET_RED.getEmoji(),
                        translations.get("trend"),
                        trendLine,
                        DynamicEmoji.BULLET_RED.getEmoji(),
                        translations.get("selling"),
                        priceLine,
                        DynamicEmoji.BULLET_RED.getEmoji(),
                        translations.get("markup"),
                        profitLine,
                        DynamicEmoji.MAP.getEmoji(),
                        data.getDirection(),
                        translations.get("parameters"),
                        data.getGivenName(),
                        translations.get("account"),
                        data.getAccountName(),
                        data.getBalance(),
                        translations.format("please-note-b-lowering-the-selling-price", Map.of("emphasis", String.format("<b>%s</b>", translations.get("please-note-b-lowering-the-selling-price.emphasis")))));
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
