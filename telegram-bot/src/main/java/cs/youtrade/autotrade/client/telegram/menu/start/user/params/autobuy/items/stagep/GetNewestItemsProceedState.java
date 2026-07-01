package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.GetNewestItemsRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.YTPTerminalDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralNewestDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.excel.generator.NewestItemsXlsxGenerator;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;

@Service
public class GetNewestItemsProceedState extends YTPTerminalDocMenuState<FcdGeneralNewestDto> {
    private final GetNewestItemsRegistry registry;
    private final GeneralEndpoint endpoint;

    public GetNewestItemsProceedState(
            UserDocMessageSender sender,
            GetNewestItemsRegistry registry,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_GET_NEWEST_ITEMS_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.get(user);
        return String.format("%s <b>Отправил все предметы в виде таблицы за последние %d часов</b>",
                DynamicEmoji.BOX.getEmoji(), data.getHrs());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }

    @Override
    public FcdGeneralNewestDto getContent(UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.getDataLastHrs(user.getChatId(), data.getHrs());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd;
    }

    @Override
    public InputFile getHeaderDoc(UserData user, FcdGeneralNewestDto fcd) {
        try {
            NewestItemsXlsxGenerator generator = new NewestItemsXlsxGenerator(fcd);
            File file = generator.generate();
            return new InputFile(file, "stats.xlsx");
        } catch (IOException e) {
            return null;
        }
    }
}
