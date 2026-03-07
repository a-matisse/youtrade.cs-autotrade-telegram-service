package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep.generator.TableBuyHistoryGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep.parent.AbstractHistoryProceedState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.buy.FcdBuyHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradePurchasedHistoryDto;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class TableBuyHistoryProceedState extends AbstractHistoryProceedState<FcdBuyHistoryFullDto> {
    public TableBuyHistoryProceedState(
            UserDocMessageSender sender,
            TableHistoryRegistry registry,
            SellDefaultEndpoint endpoint,
            TableBuyHistoryGenerator generator
    ) {
        super(sender, registry, endpoint, generator);
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return "📦 <b>История покупок</b>"
                + "\n━━━━━━━━━━";
    }

    @Override
    public RestAnswer<FcdBuyHistoryFullDto> getHistory(long chatId, int days) {
        return endpoint.getBuyHistory(chatId, days);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_P_BUY;
    }

    @Override
    public String getHeaderDocText(UserData user, FcdBuyHistoryFullDto content) {
        var fcd = content.getParams();
        return String.format("""
                        👤 <b>Профиль</b>
                        <blockquote>• ID: <b>%s</b>
                        • params-ID: <b>%s</b>
                        • Имя: <b>%s</b></blockquote>
                        
                        📊 <b>Статистика</b>
                        <blockquote>• Объем: <b>$%.2f</b>
                        • Количество: <b>%d шт.</b></blockquote>
                        
                        <b>%s</b> → <b>%s</b>
                        """,
                // Профиль
                fcd.getTdId(),
                fcd.getGivenName(),
                fcd.getTdpId(),
                // Статистика
                sumFromFcd(content),
                countFromFcd(content),
                // Направление
                fcd.getSource().getMarketName(),
                fcd.getDestination().getMarketName()
        );
    }

    private double sumFromFcd(FcdBuyHistoryFullDto content) {
        return content
                .getDtos()
                .stream()
                .flatMap(history -> history.getOnSellList().stream())
                .mapToDouble(YouTradePurchasedHistoryDto::getBuyPrice)
                .sum();
    }

    private long countFromFcd(FcdBuyHistoryFullDto content) {
        return content
                .getDtos()
                .stream()
                .mapToLong(history -> history.getOnSellList().size())
                .sum();
    }
}
