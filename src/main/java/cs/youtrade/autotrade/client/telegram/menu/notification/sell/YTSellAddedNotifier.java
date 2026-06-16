package cs.youtrade.autotrade.client.telegram.menu.notification.sell;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellAddedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTSellAddedNotifier extends YTTextNotifier<YTSellAddedNotification> {
    public YTSellAddedNotifier(UserTextMessageSender sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTSellAddedNotification data) {
        return String.format("""
                        %s <b>Выставлен на продажу</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s Интервал: от <b>$%.2f</b> до <b>$%.2f</b>
                        %s Цена покупки: <b>$%.2f</b>
                        <i>Дата покупки: <b>%s</b></i></blockquote>
                        
                        %s
                        """,
                DynamicEmoji.LOADING.getEmoji(),
                data.getItemName(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getMinPrice(), data.getMaxPrice(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getBuyPrice(),
                data.getBoughtAt(),
                data.getTokenInfoStr()
        );
    }
}
