package cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserParamsRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ChangeNameOption;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ParamsRenameProceedState extends AbstractTerminalTextMenuState {
    private static final ChangeNameOption opt = ChangeNameOption.PARAM;

    private final UserParamsRenameRegistry registry;
    private final GeneralEndpoint endpoint;

    public ParamsRenameProceedState(
            UserTextMessageSender sender,
            UserParamsRenameRegistry registry,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_RENAME_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.changeName(user.getChatId(), opt.name(), data.getId(), data.getValue());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return "Новое имя параметров установлено";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PARAMS;
    }
}
