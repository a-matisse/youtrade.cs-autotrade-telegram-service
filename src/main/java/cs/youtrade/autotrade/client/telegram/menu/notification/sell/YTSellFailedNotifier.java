package cs.youtrade.autotrade.client.telegram.menu.notification.sell;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellFailedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTSellFailedNotifier extends YTTextNotifier<YTSellFailedNotification> {
    public YTSellFailedNotifier(UserTextMessageSender sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTSellFailedNotification data) {
        return String.format("""
                        %s <b>Ордер продажи отменён</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s Возвращено: <b>$%s</b>
                        %s Цена продажи: <b>$%.2f</b>
                        <i>Дата покупки: <b>%s</b></i></blockquote>
                        
                        %s
                        """,
                DynamicEmoji.ORANGE.getEmoji(),
                data.getItemName(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getRefunded(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getSoldFor(),
                data.getPurchasedAt(),
                data.getTokenInfoStr()
        );
    }
}
