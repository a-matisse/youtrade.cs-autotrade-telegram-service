package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.stage1;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.YTPTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.TableV2SellingData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.TableV2SellingRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.stage1.generator.TableV2SellingGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetFullDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;

@Service
@Log4j2
public class TableV2SellingListState extends YTPTableState<FcdSellListGetFullDto> {
    private final PortfolioTableRegistry tableRegistry;
    private final TableV2SellingRegistry registry;
    private final TableV2SellingGenerator generator;
    private final BotTexts texts;

    public TableV2SellingListState(
            UserDocMessageSender sender,
            PortfolioTableRegistry tableRegistry,
            TableV2SellingRegistry registry,
            TableV2SellingGenerator generator,
            BotTexts texts
    ) {
        super(sender, texts);
        this.registry = registry;
        this.tableRegistry = tableRegistry;
        this.generator = generator;
        this.texts = texts;
    }

    @Override
    public UserMenu executeDocument(TelegramClient bot, Document document, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.TableV2Selling");
        try {
            File tmp = downloadFile(bot, document);
            var data = registry.getOrCreate(userData, TableV2SellingData::new);
            var toPost = generator.handleFile(tmp, botTexts.forUser(userData, "table.TableV2Selling"));
            data.setDtos(toPost);
            return UserMenu.PORTFOLIO_V2_SELLING_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, userData, String.format("#1: %s",
                    translations.get("could-not-download-the-file")));
            return UserMenu.PORTFOLIO;
        }
    }

    @Override
    public FcdSellListGetFullDto getContent(UserData userData) {
        return (FcdSellListGetFullDto) tableRegistry
                .getOrCreate(userData, PortfolioTableData::new)
                .getData();
    }

    @Override
    public InputFile getHeaderDoc(UserData userData, FcdSellListGetFullDto content) {
        try {
            return new InputFile(generator.createFile(content.getDtos(), botTexts.forUser(userData, "table.TableV2Selling")), "ycs_showcase.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_V2_SELLING_STAGE_1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.TableV2Selling");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote><i>%s</i>
                        • <b>%s</b>
                        • <b>%s</b></blockquote>
                        """,
                        DynamicEmoji.EXCEL.getEmoji(),
                        translations.get("your-listings"),
                        translations.get("fill-in-the-fields-to"),
                        translations.get("change-the-item-s-price-range"),
                        translations.get("remove-it-from-sale"));
    }

    @Override
    public String getHeaderDocText(UserData userData, FcdSellListGetFullDto content) {
        var translations = botTexts.forUser(userData, "portfolio.TableV2Selling");
        var fcd = content.getParams();
        return String.format("""
                        %s
                        
                        %s <b>%s</b>
                        <blockquote>• %s: <b>$%.2f</b>
                        • %s: <b>$%.2f</b>
                        • %s: <b>%.2f%%</b>%s</blockquote>
                        
                        %s
                        """,
                        fcd.getProfileStr(userData, texts.forUser(userData, "summary.ParamsSummary")),
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

    private String profitBankCalc(FcdSellListGetFullDto content, LocalizedTexts translations) {
        if (content.getFTotalProfit() <= 0) return "";
        return String.format("""

                • %s: <b>%.2f%%</b>""",
                translations.get("return-on-capital"),
                content.getFTotalProfit() * 100);
    }
}
