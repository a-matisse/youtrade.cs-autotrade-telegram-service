package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.YTPTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.TableV2SellingData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.TableV2SellingRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.stage1.generator.TableV2SellingGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.v2.SellV2SellingEndpoint;
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
    private final SellV2SellingEndpoint endpoint;
    private final TableV2SellingRegistry registry;
    private final TableV2SellingGenerator generator;

    public TableV2SellingListState(
            UserDocMessageSender sender,
            SellV2SellingEndpoint endpoint,
            TableV2SellingRegistry registry,
            TableV2SellingGenerator generator
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
        this.generator = generator;
    }

    @Override
    public UserMenu executeDocument(TelegramClient bot, Document document, UserData userData) {try {
        File tmp = downloadFile(bot, document);
        var data = registry.getOrCreate(userData, TableV2SellingData::new);
        var toPost = generator.handleFile(tmp);
        data.setDtos(toPost);
        return UserMenu.PORTFOLIO_V2_SELLING_STAGE_P;
    } catch (Exception e) {
        log.error("Ошибка загрузки файла диапазонов", e);
        sender.sendTextMes(bot, userData, "#1: Не удалось загрузить файл.");
        return UserMenu.PORTFOLIO;
    }
    }

    @Override
    public FcdSellListGetFullDto getContent(UserData userData) {
        var restAns = endpoint.getSelling(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd;
    }

    @Override
    public InputFile getHeaderDoc(UserData userData, FcdSellListGetFullDto content) {
        try {
            return new InputFile(generator.createFile(content.getDtos()), "ycs_showcase.xlsx");
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
        return String.format("""
                        %s <b>Витрина пользователя</b>
                        <blockquote><i>Заполните поля, чтобы</i>
                        • <b>Изменить ценовую вилку предмета</b>
                        • <b>Снять его с продажи</b></blockquote>
                        """,
                DynamicEmoji.EXCEL.getEmoji());
    }

    @Override
    public String getHeaderDocText(UserData userData, FcdSellListGetFullDto content) {
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

    private String profitBankCalc(FcdSellListGetFullDto content) {
        if (content.getFTotalProfit() <= 0) return "";
        return String.format("\n• Доход (от банка): <b>%.2f%%</b>",
                content.getFTotalProfit() * 100);
    }
}
