package cs.youtrade.autotrade.client.telegram.menu.notification.buy.bargain;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.buy.YTBuyCompletedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class YTBargainCreatedNotifier extends YTTextNotifier<YTBuyCompletedNotification> {
    public YTBargainCreatedNotifier(
            UserTextMessageSender sender,
            TelegramClient bot
    ) {
        super(sender, bot);
    }

    @Override
    public String getText(YTBuyCompletedNotification data) {
        String discount = "";
        if (data.getMarketPrice() != null && data.getMarketPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percent = BigDecimal.ONE
                    .subtract(data.getPrice().divide(data.getMarketPrice(), 16, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(100));
            discount = String.format("\n%s Скидка: <b>%.2f%%</b>",
                    DynamicEmoji.BULLET_YELLOW.getEmoji(), percent);
        }

        return String.format("""
                        %s <b>Торг создан</b>

                        <code><b>%s</b></code>
                        <blockquote>%s Предложено: <b>$%.2f</b>%s</blockquote>

                        %s
                        """,
                DynamicEmoji.LOADING.getEmoji(), data.getItemName(),
                DynamicEmoji.BULLET_YELLOW.getEmoji(), data.getPrice(), discount,
                data.getTokenInfoStr()
        );
    }
}
