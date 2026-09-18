package cs.youtrade.autotrade.client.telegram.menu.notification.buy.bargain;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.buy.YTBargainNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTBargainFailedNotifier extends YTTextNotifier<YTBargainNotification> {
    public YTBargainFailedNotifier(
            UserTextMessageSender sender,
            TelegramClient bot
    ) {
        super(sender, bot);
    }

    @Override
    public String getText(YTBargainNotification data) {
        return String.format("""
                        %s <b>Торг прошел неудачно</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s Куплено за: <b>$%.2f</b>
                        %s Скидка: <b>%.2f%%</b></blockquote>
                        
                        %s
                        """,
                DynamicEmoji.OFF.getEmoji(),
                data.getItemName(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getPrice(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getAdditionalProfit(),
                data.getTokenInfoStr()
        );
    }
}
