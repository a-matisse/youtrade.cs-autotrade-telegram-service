package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.restrict.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.YTPTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.restrict.TableRestrictData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.restrict.TableRestrictRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.restrict.stage1.generator.TableRestrictGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellRestrictEndpoint;
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
public class TableRestrictListState extends YTPTableState<List<FcdSellRestrictGetDto>> {
    private final TableRestrictRegistry registry;
    private final TableRestrictGenerator generator;
    private final SellRestrictEndpoint endpoint;

    public TableRestrictListState(
            UserDocMessageSender sender,
            TableRestrictRegistry registry,
            TableRestrictGenerator generator,
            SellRestrictEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.generator = generator;
        this.endpoint = endpoint;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return """
                📋 <b>Запрет предметов к продаже</b>
                
                Пожалуйста, заполните желаемые поля в пустых колонках.
                
                🛑 <b>Важно</b>
                • не изменяйте имя файла
                • не редактируйте первые ячейки таблицы
                """;
    }

    @Override
    public List<FcdSellRestrictGetDto> getContent(UserData user) {
        var restAns = endpoint.getRestrictions(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd.getData();
    }

    @Override
    public InputFile getHeaderDoc(UserData user, List<FcdSellRestrictGetDto> content) {
        try {
            return new InputFile(generator.createFile(content), "restricted_all.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_RESTRICT_STAGE_1;
    }

    @Override
    public UserMenu executeDocument(TelegramClient bot, Document document, UserData user) {
        try {
            File tmp = downloadFile(bot, document);
            var data = registry.getOrCreate(user, TableRestrictData::new);
            var toPost = generator.handleFile(tmp);
            data.setDtos(toPost);
            return UserMenu.PORTFOLIO_RESTRICT_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, user, "#1: Не удалось загрузить файл.");
            return UserMenu.PORTFOLIO;
        }
    }
}
