package cs.youtrade.autotrade.client.telegram.menu.notification.sell;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellCompletedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;

@Component
public class YTSellCompletedNotifier extends YTTextNotifier<YTSellCompletedNotification> {
    public YTSellCompletedNotifier(TelegramSendMessageService sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTSellCompletedNotification data) {
        BigDecimal profit = data.getProfit();
        String emojiColor = profit.compareTo(BigDecimal.ZERO) >= 0
                ? DynamicEmoji.ON.getEmoji()
                : DynamicEmoji.OFF.getEmoji();
        return String.format("""
                        %s <b>Ордер продажи исполнен</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s Прибыль: <b>%.2f%%</b>
                        %s Куплено за: <b>$%.2f</b>
                        %s Продано за: <b>$%.2f</b>
                        <i>Дата покупки: <b>%s</b></i></blockquote>
                        
                        %s
                        """,
                emojiColor,
                data.getItemName(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), profit,
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getBoughtFor(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getSoldFor(),
                data.getPurchasedAt(),
                data.getTokenInfoStr()
        );
    }
}
