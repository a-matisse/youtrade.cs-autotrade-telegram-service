package cs.youtrade.autotrade.client.telegram.menu.notification.buy.bargain;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

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
            TelegramClient bot,
            BotTexts botTexts
    ) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTBargainNotification data) {
        var translations = botTexts.forUser(user, "notification.BargainNotifications");
        String priceFactor = "";
        if (data.getPriceFactor() != null) {
            String sign = data.getPriceFactor().compareTo(BigDecimal.ZERO) >= 0
                    ? DynamicEmoji.HIGHER.getEmoji() + String.format(" %s",
                            translations.get("more-expensive-by"))
                    : DynamicEmoji.LOWER.getEmoji() + String.format(" %s",
                            translations.get("cheaper-by"));
            priceFactor = String.format(" (%s <b>%.2f%%</b>)", sign, data.getPriceFactor());
        }

        String details = String.format("""
                %s %s: <b>$%.2f</b>%s
                %s %s: <b>%.2f%%</b>""",
                DynamicEmoji.BULLET_YELLOW.getEmoji(),
                translations.get("price"),
                data.getPrice(),
                priceFactor,
                DynamicEmoji.BULLET_YELLOW.getEmoji(),
                translations.get("discount"),
                data.getAdditionalProfit());
        String trendLine = formatMessageDataMap(data.getTrendMap(), value ->
                String.format("<b>%.2f%%</b>", value));
        String priceLine = formatMessageDataMap(data.getPriceMap(), value ->
                String.format("<b>$%.2f</b>", value));
        String profitLine = formatMessageDataMap(data.getPercentMap(), value ->
                String.format("<b>%.2f%%</b>", value));

        return String.format("""
                        %s <b>%s</b>
                        
                        <code><b>%s</b></code>
                        <blockquote expandable>%s
                        
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
                        DynamicEmoji.ON.getEmoji(),
                        translations.get("bargain-completed-successfully"),
                        data.getItemName(),
                        details,
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

    private String formatMessageDataMap(Map<String, Double> map, Function<Double, String> formatter) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + formatter.apply(entry.getValue()))
                .collect(Collectors.joining(" — "));
    }
}
