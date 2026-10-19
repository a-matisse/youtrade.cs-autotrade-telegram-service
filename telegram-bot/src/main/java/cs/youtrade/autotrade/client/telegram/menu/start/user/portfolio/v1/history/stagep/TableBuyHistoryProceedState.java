package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep;

import java.util.Map;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
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
    private final BotTexts texts;

    public TableBuyHistoryProceedState(
            UserDocMessageSender sender,
            PortfolioTableRegistry registry,
            TableBuyHistoryGenerator generator,
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
                translations.get("purchase-history"));
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
        var translations = botTexts.forUser(user, "portfolio.history.TableHistoryResult");
        var fcd = content.getParams();
        return String.format("""
                        %s
                        
                        %s <b>%s</b>
                        <blockquote>• %s: <b>$%.2f</b>
                        • %s</blockquote>
                        
                        %s
                        """,
                        fcd.getProfileStr(user, texts.forUser(user, "summary.ParamsSummary")),
                        DynamicEmoji.GRAPH.getEmoji(),
                        translations.get("statistics"),
                        translations.get("volume"),
                        sumFromFcd(content),
                        translations.format("quantity", Map.of("value1", String.format("<b>%s</b>", translations.format("item-count", Map.of("count", countFromFcd(content)))))),
                        fcd.getDirection());
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
