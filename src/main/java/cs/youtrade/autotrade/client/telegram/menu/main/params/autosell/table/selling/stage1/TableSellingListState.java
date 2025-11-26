package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.selling.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.AbstractTableState;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.selling.TableSellingData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.selling.TableSellingRegistry;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.selling.stage1.generator.TableSellingGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellListEndpoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;

@Service
@Log4j2
public class TableSellingListState extends AbstractTableState<FcdSellListGetFullDto> {
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
    public String getHeaderText(UserData user) {
        return "📦 Список ожидаемых предметов";
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
        return String.format("""
                        🔢 Объем: $%.2f
                        💰 Прогнозируемый заработок: $%.2f
                        📈 Прогнозируемая прибыль: %.2f%%
                        """,
                content.getFVolume(),
                content.getFEarn(),
                content.getFProfit() * 100
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TABLE_SELLING_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        Message message = update.getMessage();
        if (!message.hasDocument()) {
            sender.sendTextMes(bot, chatId, "#0: В полученном сообщении не найден документ. Возврат обратно в меню...");
            return UserMenu.TABLE;
        }

        try {
            File tmp = downloadFile(bot, message.getDocument());
            var data = registry.getOrCreate(user, TableSellingData::new);
            var toPost = generator.handleFile(tmp);
            data.setDtos(toPost);
            return UserMenu.TABLE_SELLING_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, chatId, "#1: Не удалось загрузить файл.");
            return UserMenu.TABLE;
        }
    }
}
