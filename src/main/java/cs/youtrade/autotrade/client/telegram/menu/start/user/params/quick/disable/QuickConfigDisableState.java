package cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.disable;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsQuickConfigEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class QuickConfigDisableState extends AbstractTerminalTextMenuState {
    private final ParamsQuickConfigEndpoint endpoint;

    public QuickConfigDisableState(
            UserTextMessageSender sender,
            ParamsQuickConfigEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_QUICK_CONFIG_DISABLE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        RestAnswer<FcdDefaultDto<Long>> restAns = endpoint.disable(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();
        return "Быстрая настройка ликвидирована";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PARAMS;
    }
}
