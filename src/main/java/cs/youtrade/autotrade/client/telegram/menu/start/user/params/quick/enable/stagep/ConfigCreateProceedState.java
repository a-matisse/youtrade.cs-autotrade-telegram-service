package cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.QuickConfigCreateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsQuickConfigInitDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsQuickConfigEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ConfigCreateProceedState extends AbstractTerminalTextMenuState {
    private final QuickConfigCreateRegistry registry;
    private final ParamsQuickConfigEndpoint endpoint;

    public ConfigCreateProceedState(
            UserTextMessageSender sender,
            QuickConfigCreateRegistry registry,
            ParamsQuickConfigEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_QUICK_CONFIG_INIT_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.remove(userData);
        var req = new FcdParamsQuickConfigInitDto(data.getPreferredTradeCapital(), data.getBuyGrade(), data.getSellGrade());
        RestAnswer<FcdDefaultDto<Long>> restAns = endpoint.init(userData.getChatId(), req);
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return """
                🟢 <b>Быстрая настройка сохранена</b>
                
                Параметры сохранены и будут применяться к новым операциям.
                """;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PARAMS;
    }
}
