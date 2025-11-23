package cs.youtrade.autotrade.client.telegram.menu.main.items.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.items.GetNewestItemsRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.AbstractTerminalDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.XlsxExporter;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.IOException;

@Service
public class GetNewestItemsProceedState extends AbstractTerminalDocMenuState {
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
    public InputFile getHeaderDoc(UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.getDataLastHrs(user.getChatId(), data.getHrs());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        try {
            var file = XlsxExporter.exportToXlsx(fcd.getData(), "Item Stats");
            return new InputFile(file, "stats.xlsx");
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_GET_NEWEST_ITEMS_STAGE_P;
    }

    @Override
    public String getHeaderText(UserData user) {
        var data = registry.get(user);
        return String.format("📦 Отправил все предметы в виде таблицы за последние %d часов", data.getHrs());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.MAIN;
    }
}
