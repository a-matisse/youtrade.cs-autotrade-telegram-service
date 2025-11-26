package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.update.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.update.UserAutoSellUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;

@Service
public class AutoSellUpdateProceedState extends AbstractTerminalTextMenuState {
    private final UserAutoSellUpdateRegistry registry;
    private final GeneralEndpoint endpoint;

    public AutoSellUpdateProceedState(
            UserTextMessageSender sender,
            UserAutoSellUpdateRegistry registry,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_P;
    }

    @Override
    public String getHeaderText(UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.changeField(user.getChatId(), data.getField(), data.getValue());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("Поле %s успешно обновлено", data.getField());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOSELL;
    }
}
