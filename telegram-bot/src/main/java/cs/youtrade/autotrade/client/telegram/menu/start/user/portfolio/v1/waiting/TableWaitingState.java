package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.waiting;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.waiting.generator.TableWaitingGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.YTPTerminalDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

@Service
@Log4j2
public class TableWaitingState extends YTPTerminalDocMenuState<FcdSellWaitFullDto> {
    private final PortfolioTableRegistry registry;
    private final TableWaitingGenerator generator;
    private final BotTexts texts;

    public TableWaitingState(
            UserDocMessageSender sender,
            PortfolioTableRegistry registry,
            TableWaitingGenerator generator,
            BotTexts texts
    ) {
        super(sender, texts);
        this.registry = registry;
        this.generator = generator;
        this.texts = texts;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.TableWaiting");
        return String.format("%s <b>%s</b>",
                DynamicEmoji.EXCEL.getEmoji(),
                translations.get("pending-items"));
    }

    @Override
    public FcdSellWaitFullDto getContent(UserData user) {
        return (FcdSellWaitFullDto) registry
                .getOrCreate(user, PortfolioTableData<FcdSellWaitFullDto>::new)
                .getData();
    }

    @Override
    public InputFile getHeaderDoc(UserData user, FcdSellWaitFullDto content) {
        try {
            return new InputFile(generator.createFile(content, botTexts.forUser(user, "table.TableWaiting")), "sell_listed.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getHeaderDocText(UserData user, FcdSellWaitFullDto content) {
        var translations = botTexts.forUser(user, "portfolio.TableWaiting");
        var fcd = content.getParams();
        return String.format("""
                        %s
                        
                        %s <b>%s</b>
                        <blockquote>• %s: <b>$%.2f</b>
                        • %s: <b>$%.2f</b>
                        • %s: <b>%.2f%%</b>%s</blockquote>
                        
                        %s
                        """,
                        fcd.getProfileStr(user, texts.forUser(user, "summary.ParamsSummary")),
                        DynamicEmoji.GRAPH.getEmoji(),
                        translations.get("statistics"),
                        translations.get("volume"),
                        content.getFVolume(),
                        translations.get("earnings"),
                        content.getFEarn(),
                        translations.get("net-return"),
                        content.getFProfit() * 100,
                        profitBankCalc(content, translations),
                        fcd.getDirection());
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_WAITING;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    private String profitBankCalc(FcdSellWaitFullDto content, LocalizedTexts translations) {
        if (content.getFTotalProfit() <= 0) return "";
        return String.format("""

                • %s: <b>%.2f%%</b>""",
                translations.get("return-on-capital"),
                content.getFTotalProfit() * 100);
    }
}
