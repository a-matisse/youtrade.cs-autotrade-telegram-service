package cs.youtrade.autotrade.client.telegram.menu.notification.payment;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import java.util.Map;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.YTPaymentNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTPaymentNotifier extends YTTextNotifier<YTPaymentNotification> {
    public YTPaymentNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTPaymentNotification data) {
        var translations = botTexts.forUser(user, "notification.YTPayment");
        return String.format("""
                        <b>%s</b>
                        
                        <blockquote><b>%s</b>
                        <tg-spoiler>%s</tg-spoiler>
                        
                        • %s: <b>%.2f %s</b>
                        • %s: <b>%s</b>
                        • %s</blockquote>
                        
                        <i>%s: %s</i>""",
                        getPaymentHeader(data, translations),
                        translations.get("transaction-id"),
                        data.getIdempotencyKey(),
                        translations.get("amount"),
                        data.getAmount().doubleValue(),
                        data.getCurrency(),
                        translations.get("type"),
                        data.getTopUpType(),
                        translations.format("purpose", Map.of("value1", String.format("<b>%s</b>", translations.get("balance-top-up")))),
                        translations.get("date"),
                        data.getPaymentTime());
    }

    private String getPaymentHeader(YTPaymentNotification data, LocalizedTexts translations) {
        return data.getSuccessful()
                ? String.format("%s %s",
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.get("payment-completed"))
                : String.format("%s %s",
                        DynamicEmoji.ERROR.getEmoji(),
                        translations.get("payment-failed"));
    }
}
