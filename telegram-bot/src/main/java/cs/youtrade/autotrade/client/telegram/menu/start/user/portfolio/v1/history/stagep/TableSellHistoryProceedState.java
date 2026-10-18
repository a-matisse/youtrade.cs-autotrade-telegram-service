package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator.TableSellHistoryGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.parent.AbstractHistoryProceedState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.sell.FcdSellHistoryFullDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableSellHistoryProceedState extends AbstractHistoryProceedState<FcdSellHistoryFullDto> {
    private final PortfolioTableRegistry registry;

    public TableSellHistoryProceedState(
            UserDocMessageSender sender,
            PortfolioTableRegistry registry,
            TableSellHistoryGenerator generator
    ) {
        super(sender, generator);
        this.registry = registry;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("%s <b>История продаж</b>",
                DynamicEmoji.EXCEL.getEmoji());
    }

    @Override
    public FcdSellHistoryFullDto getContent(UserData user) {
        return (FcdSellHistoryFullDto) registry
                .getOrCreate(user, PortfolioTableData<FcdSellHistoryFullDto>::new)
                .getData();
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_P_SELL;
    }

    @Override
    public String getHeaderDocText(UserData user, FcdSellHistoryFullDto content) {
        var fcd = content.getParams();
        return String.format("""
                        %s
                        
                        %s <b>Статистика</b>
                        <blockquote>• Объем: <b>$%.2f</b>
                        • Заработок: <b>$%.2f</b>
                        • Доход (чистый): <b>%.2f%%</b>%s</blockquote>
                        
                        %s
                        """,
                // Профиль
                fcd.getProfileStr(user),
                // Статистика
                DynamicEmoji.GRAPH.getEmoji(),
                content.getFVolume(),
                content.getFEarn(),
                content.getFProfit() * 100,
                profitBankCalc(content),
                // Направление
                fcd.getDirection()
        );
    }

    private String profitBankCalc(FcdSellHistoryFullDto content) {
        if (content.getFTotalProfit() <= 0) return "";
        return String.format("\n• Доход (от банка): <b>%.2f%%</b>",
                content.getFTotalProfit() * 100);
    }
}
