package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import java.util.Map;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTInvBaseRestrictNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static cs.youtrade.autotrade.client.util.notification.YTNotificationType.PORTFOLIO_RESTRICTED;

@Component
public class YTInvBaseRestrictNotifier extends YTTextNotifier<YTInvBaseRestrictNotification> {
    public YTInvBaseRestrictNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public boolean shouldNotify(YTInvBaseRestrictNotification data) {
        return data.getAmount() > 0;
    }

    @Override
    public String getText(UserData user, YTInvBaseRestrictNotification data) {
        var translations = botTexts.forUser(user, "notification.InventoryNotifications");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b>
                        • %s: <b>%s</b></blockquote>
                        """,
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.format("items-successfully", Map.of("argument2", String.valueOf(getAction(data, translations)))),
                        translations.get("quantity"),
                        data.getAmount(),
                        translations.get("account"),
                        data.getTokenName());
    }

    private String getAction(YTInvBaseRestrictNotification data, LocalizedTexts translations) {
        return data.getType().equals(PORTFOLIO_RESTRICTED) ? translations.get("blocked-from-sale") : translations.get("allowed-for-sale");
    }
}
