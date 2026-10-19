package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
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
    private final BotTexts texts;

    public TableSellHistoryProceedState(
            UserDocMessageSender sender,
            PortfolioTableRegistry registry,
            TableSellHistoryGenerator generator,
            BotTexts texts
    ) {
        super(sender, generator, texts);
        this.registry = registry;
        this.texts = texts;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.history.TableHistoryResult");
        return String.format("%s <b>%s</b>",
                DynamicEmoji.EXCEL.getEmoji(),
                translations.get("sales-history"));
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
        var translations = botTexts.forUser(user, "portfolio.history.TableHistoryResult");
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

    private String profitBankCalc(FcdSellHistoryFullDto content, LocalizedTexts translations) {
        if (content.getFTotalProfit() <= 0) return "";
        return String.format("""

                • %s: <b>%.2f%%</b>""",
                translations.get("return-on-capital"),
                content.getFTotalProfit() * 100);
    }
}
