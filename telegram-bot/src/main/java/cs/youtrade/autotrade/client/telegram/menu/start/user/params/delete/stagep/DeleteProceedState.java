package cs.youtrade.autotrade.client.telegram.menu.start.user.params.delete.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.delete.ParamsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.delete.ParamsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsDeleteResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator.getErrorText;

@Service
public class DeleteProceedState extends YTPTerminalTextMenuState {
    private final ParamsDeleteRegistry registry;
    private final ParamsEndpoint paramsEndpoint;

    public DeleteProceedState(
            UserTextMessageSender sender,
            ParamsDeleteRegistry registry,
            ParamsEndpoint paramsEndpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_DELETE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsDelete");
        ParamsDeleteData deleteData = registry.remove(userData);
        RestAnswer<FcdParamsDeleteResDto> restAns = paramsEndpoint.proceedDelete(userData.getChatId(), deleteData.getCallback());
        if (restAns.getStatus() >= 300)
            return getErrorText(translations.inBundle("common.Interface"));

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("%s <b>%s</b>",
                DynamicEmoji.SUCCESS.getEmoji(),
                translations.format("parameters-deleted-params-id", Map.of("value1", String.format("<code>%d</code>", fcd.getTdpId()))));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PARAMS;
    }
}
