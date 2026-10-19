package cs.youtrade.autotrade.client.telegram.menu.start.user.pswitch.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.pswitch.ParamsSwitchRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ParamsSwitchProceedState extends YTPTerminalTextMenuState {
    private final ParamsSwitchRegistry registry;
    private final ParamsEndpoint endpoint;

    public ParamsSwitchProceedState(
            UserTextMessageSender sender,
            ParamsSwitchRegistry registry,
            ParamsEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_SWITCH_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsSwitch");
        var data = registry.remove(userData);
        var restAns = endpoint.switchP(userData.getChatId(), data.getInput());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return String.format("%s <b>%s</b>",
                DynamicEmoji.SUCCESS.getEmoji(),
                translations.format("current-parameters-switched-to", Map.of("value1", String.format("<code>%s</code>", fcd.getData().getGivenName()))));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.USER;
    }
}
