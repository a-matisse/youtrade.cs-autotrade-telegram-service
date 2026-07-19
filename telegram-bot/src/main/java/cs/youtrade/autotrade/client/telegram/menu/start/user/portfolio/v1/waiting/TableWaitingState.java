package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.waiting;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.waiting.generator.TableWaitingGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.YTPTerminalDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

@Service
@Log4j2
public class TableWaitingState extends YTPTerminalDocMenuState<FcdSellWaitFullDto> {
    private final SellDefaultEndpoint endpoint;
    private final TableWaitingGenerator generator;

    public TableWaitingState(
            UserDocMessageSender sender,
            SellDefaultEndpoint endpoint,
            TableWaitingGenerator generator
    ) {
        super(sender);
        this.endpoint = endpoint;
        this.generator = generator;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("%s <b>Список ожидаемых предметов</b>",
                DynamicEmoji.EXCEL.getEmoji());
    }

    @Override
    public FcdSellWaitFullDto getContent(UserData user) {
        var restAns = endpoint.getSellWaiting(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd;
    }

    @Override
    public InputFile getHeaderDoc(UserData user, FcdSellWaitFullDto content) {
        try {
            return new InputFile(generator.createFile(content), "sell_listed.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getHeaderDocText(UserData user, FcdSellWaitFullDto content) {
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

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_WAITING;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    private String profitBankCalc(FcdSellWaitFullDto content) {
        if (content.getFTotalProfit() <= 0) return "";
        return String.format("\n• Доход (от банка): <b>%.2f%%</b>",
                content.getFTotalProfit() * 100);
    }
}
