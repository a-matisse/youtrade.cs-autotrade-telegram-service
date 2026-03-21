package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.YTPTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.TableChangeData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.TableChangeRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.stage1.generator.TableChangeGroupGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.stage1.generator.TableChangeSingleGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangeGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangePostDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellChangeEndpoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Log4j2
public class TableChangeListState extends YTPTableState<List<FcdSellChangeGetDto>> {
    private final TableChangeRegistry registry;
    private final SellChangeEndpoint endpoint;
    private final TableChangeSingleGenerator singleGenerator;
    private final TableChangeGroupGenerator groupGenerator;

    public TableChangeListState(
            UserDocMessageSender sender,
            TableChangeRegistry registry,
            SellChangeEndpoint endpoint,
            TableChangeSingleGenerator singleGenerator,
            TableChangeGroupGenerator groupGenerator
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
        this.singleGenerator = singleGenerator;
        this.groupGenerator = groupGenerator;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return """
                📋 <b>Ценовые диапазоны</b>
                
                Пожалуйста, заполните желаемые поля в пустых колонках.
                
                🛑 <b>Важно</b>
                • не изменяйте имя файла
                • не редактируйте первые ячейки таблицы
                """;
    }

    @Override
    public List<FcdSellChangeGetDto> getContent(UserData user) {
        return List.of();
    }

    @Override
    public InputFile getHeaderDoc(UserData user, List<FcdSellChangeGetDto> content) {
        var data = registry.getOrCreate(user, TableChangeData::new);
        try {
            return switch (data.getType()) {
                case SINGLE -> processSingleOutput(user);
                case GROUPED -> processGroupOutput(user);
                case RETURN -> throw new IllegalStateException("Cannot process RETURN state");
            };
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_CHANGE_STAGE_1;
    }

    @Override
    public UserMenu executeDocument(TelegramClient bot, Document document, UserData user) {
        try {
            File tmp = downloadFile(bot, document);
            var data = registry.getOrCreate(user, TableChangeData::new);
            List<FcdSellChangePostDto> toPost = switch (data.getType()) {
                case SINGLE -> singleGenerator.handleFile(tmp);
                case GROUPED -> groupGenerator.handleFile(tmp);
                case RETURN -> throw new IllegalStateException("Cannot process RETURN state");
            };
            data.setDtos(toPost);
            return UserMenu.PORTFOLIO_CHANGE_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, user, "#1: Не удалось загрузить файл.");
            return UserMenu.PORTFOLIO;
        }
    }

    private InputFile processSingleOutput(UserData user) throws IOException {
        var restAns = endpoint.getChanges(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return new InputFile(singleGenerator.createFile(fcd.getData()), "price_intervals.xlsx");
    }

    private InputFile processGroupOutput(UserData user) throws IOException {
        var restAns = endpoint.getChangesGrouped(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return new InputFile(groupGenerator.createFile(fcd.getData()), "price_intervals.xlsx");
    }
}
