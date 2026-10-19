package cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.ParamsCreateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class CreateProceedState extends YTPTerminalTextMenuState {
    private final ParamsCreateRegistry registry;
    private final ParamsEndpoint endpoint;

    public CreateProceedState(
            UserTextMessageSender sender,
            ParamsCreateRegistry registry,
            ParamsEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_CREATE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsCreate");
        var data = registry.remove(userData);
        RestAnswer<FcdDefaultDto<Long>> restAns = endpoint.create(userData.getChatId(), data.getSource(), data.getDestination(), "");
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("%s <b>%s</b>",
                DynamicEmoji.SUCCESS.getEmoji(),
                translations.format("parameters-created-params-id", Map.of("value1", String.format("<code>%d</code>", fcd.getData()))));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PARAMS;
    }
}
