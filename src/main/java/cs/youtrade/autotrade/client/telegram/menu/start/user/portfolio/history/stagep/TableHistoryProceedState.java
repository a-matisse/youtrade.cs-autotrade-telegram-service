package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep.generator.TableHistoryGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.AbstractTerminalDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.FcdSellHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;

@Service
@Log4j2
public class TableHistoryProceedState extends AbstractTerminalDocMenuState<FcdSellHistoryFullDto> {
    private final TableHistoryRegistry registry;
    private final SellDefaultEndpoint endpoint;
    private final TableHistoryGenerator generator;

    public TableHistoryProceedState(
            UserDocMessageSender sender,
            TableHistoryRegistry registry,
            SellDefaultEndpoint endpoint,
            TableHistoryGenerator generator
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
        this.generator = generator;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return "📦 <b>История продаж</b>"
                + "\n━━━━━━━━━━";
    }

    @Override
    public FcdSellHistoryFullDto getContent(UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.getSellHistory(user.getChatId(), data.getPeriod());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd;
    }

    @Override
    public InputFile getHeaderDoc(UserData user, FcdSellHistoryFullDto content) {
        try {
            File output = generator.createFile(content);
            return new InputFile(output, "sell_history.xlsx");
        } catch (IOException e) {
            log.error("Ошибка загрузки файла истории", e);
            return null;
        }
    }

    @Override
    public String getHeaderDocText(UserData user, FcdSellHistoryFullDto content) {
        var fcd = content.getParams();
        return String.format("""
                        👤 <b>Профиль</b>
                        <blockquote>• ID: <b>%s</b>
                        • params-ID: <b>%s</b>
                        • Имя: <b>%s</b></blockquote>
                        
                        📊 <b>Статистика</b>
                        <blockquote>• Объем: <b>$%.2f</b>
                        • Заработок: <b>$%.2f</b>
                        • Доход (чистый): <b>%.2f%%</b>%s</blockquote>
                        
                        <b>%s</b> → <b>%s</b>
                        """,
                // Профиль
                fcd.getTdId(),
                fcd.getGivenName(),
                fcd.getTdpId(),
                // Статистика
                content.getFVolume(),
                content.getFEarn(),
                content.getFProfit() * 100,
                profitBankCalc(content),
                // Направление
                fcd.getSource().getMarketName(),
                fcd.getDestination().getMarketName()
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_P;
    }

    private String profitBankCalc(FcdSellHistoryFullDto content) {
        if (content.getFTotalProfit() <= 0) return "";
        return String.format("\n• Доход (от банка): <b>%.2f%%</b>",
                content.getFTotalProfit() * 100);
    }
}
