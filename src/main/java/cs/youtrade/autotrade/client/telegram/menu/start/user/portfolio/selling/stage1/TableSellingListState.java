package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.selling.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.YTPTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.selling.TableSellingData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.selling.TableSellingRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.selling.stage1.generator.TableSellingGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellListEndpoint;
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
public class TableSellingListState extends YTPTableState<FcdSellListGetFullDto> {
    private final TableSellingRegistry registry;
    private final SellListEndpoint endpoint;
    private final TableSellingGenerator generator;

    public TableSellingListState(
            UserDocMessageSender sender,
            TableSellingRegistry registry,
            SellListEndpoint endpoint,
            TableSellingGenerator generator
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
        this.generator = generator;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("%s <b>Предметы на продаже</b>",
                DynamicEmoji.EXCEL.getEmoji());
    }

    @Override
    public FcdSellListGetFullDto getContent(UserData user) {
        var restAns = endpoint.getList(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd;
    }

    @Override
    public InputFile getHeaderDoc(UserData user, FcdSellListGetFullDto content) {
        try {
            return new InputFile(generator.createFile(content.getDtos()), "sell_listed.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getHeaderDocText(UserData user, FcdSellListGetFullDto content) {
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
                fcd.getProfileStr(),
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
        return UserMenu.PORTFOLIO_SELLING_STAGE_1;
    }

    @Override
    public UserMenu executeDocument(TelegramClient bot, Document document, UserData user) {
        try {
            File tmp = downloadFile(bot, document);
            var data = registry.getOrCreate(user, TableSellingData::new);
            var toPost = generator.handleFile(tmp);
            data.setDtos(toPost);
            return UserMenu.PORTFOLIO_SELLING_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, user, "#1: Не удалось загрузить файл.");
            return UserMenu.PORTFOLIO;
        }
    }

    private String profitBankCalc(FcdSellListGetFullDto content) {
        if (content.getFTotalProfit() <= 0) return "";
        return String.format("\n• Доход (от банка): <b>%.2f%%</b>",
                content.getFTotalProfit() * 100);
    }
}
