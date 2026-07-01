package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.QuickConfigCreateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsQuickConfigInitDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsQuickConfigEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ConfigCreateProceedState extends YTPTerminalTextMenuState {
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
        return UserMenu.USER_QUICK_CONFIG_INIT_STAGE_P;
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

        return String.format("%s <b>Быстрая настройка включена</b>",
                DynamicEmoji.ON.getEmoji());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.USER;
    }
}
