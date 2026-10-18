package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator.TableBuyHistoryGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.parent.AbstractHistoryProceedState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.buy.FcdBuyHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradePurchasedHistoryDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableBuyHistoryProceedState extends AbstractHistoryProceedState<FcdBuyHistoryFullDto> {
    private final PortfolioTableRegistry registry;

    public TableBuyHistoryProceedState(
            UserDocMessageSender sender,
            PortfolioTableRegistry registry,
            TableBuyHistoryGenerator generator
    ) {
        super(sender, generator);
        this.registry = registry;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("%s <b>История покупок</b>",
                DynamicEmoji.EXCEL.getEmoji());
    }

    @Override
    public FcdBuyHistoryFullDto getContent(UserData user) {
        return (FcdBuyHistoryFullDto) registry
                .getOrCreate(user, PortfolioTableData<FcdBuyHistoryFullDto>::new)
                .getData();
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_P_BUY;
    }

    @Override
    public String getHeaderDocText(UserData user, FcdBuyHistoryFullDto content) {
        var fcd = content.getParams();
        return String.format("""
                        %s
                        
                        %s <b>Статистика</b>
                        <blockquote>• Объем: <b>$%.2f</b>
                        • Количество: <b>%d шт.</b></blockquote>
                        
                        %s
                        """,
                // Профиль
                fcd.getProfileStr(user),
                // Статистика
                DynamicEmoji.GRAPH.getEmoji(),
                sumFromFcd(content),
                countFromFcd(content),
                // Направление
                fcd.getDirection()
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
