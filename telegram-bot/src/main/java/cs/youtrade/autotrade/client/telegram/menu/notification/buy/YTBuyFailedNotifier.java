package cs.youtrade.autotrade.client.telegram.menu.notification.buy;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.buy.YTBuyFailedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTBuyFailedNotifier extends YTTextNotifier<YTBuyFailedNotification> {
    public YTBuyFailedNotifier(UserTextMessageSender sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTBuyFailedNotification data) {
        return String.format("""
                        %s <b>Покупка отменена</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s Возвращено: <b>$%s</b>
                        %s Причина: <b>%s</b></blockquote>
                        
                        %s
                        """,
                DynamicEmoji.ERROR.getEmoji(),
                data.getItemName(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getRefunded().toPlainString(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getReason(),
                data.getTokenInfoStr()
        );
    }
}
