package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep.generator.TableSellHistoryGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep.parent.AbstractHistoryProceedState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.sell.FcdSellHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableSellHistoryProceedState extends AbstractHistoryProceedState<FcdSellHistoryFullDto> {
    public TableSellHistoryProceedState(
            UserDocMessageSender sender,
            TableHistoryRegistry registry,
            SellDefaultEndpoint endpoint,
            TableSellHistoryGenerator generator
    ) {
        super(sender, registry, endpoint, generator);
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return "📦 <b>История продаж</b>"
                + "\n━━━━━━━━━━";
    }

    @Override
    public RestAnswer<FcdSellHistoryFullDto> getHistory(long chatId, int days) {
        return endpoint.getSellHistory(chatId, days);
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
                        
                        📊 <b>Статистика</b>
                        <blockquote>• Объем: <b>$%.2f</b>
                        • Заработок: <b>$%.2f</b>
                        • Доход (чистый): <b>%.2f%%</b>%s</blockquote>
                        
                        %s
                        """,
                // Профиль
                fcd.getProfileStr(),
                // Статистика
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
