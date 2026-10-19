package cs.youtrade.autotrade.client.telegram.menu.start.user.params.delete.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.delete.ParamsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.delete.ParamsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsDeleteReqDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator.getErrorText;

@Service
public class DeleteRequestState extends YTPTextMenuState<DeleteRequestMenu> {
    private final ParamsDeleteRegistry registry;
    private final ParamsEndpoint paramsEndpoint;

    public DeleteRequestState(
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
        return UserMenu.PARAMS_DELETE_STAGE_1;
    }

    @Override
    public DeleteRequestMenu getOption(String optionStr) {
        return DeleteRequestMenu.valueOf(optionStr);
    }

    @Override
    public DeleteRequestMenu[] getOptions(UserData userData) {
        return DeleteRequestMenu.values();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsDelete");
        RestAnswer<FcdParamsDeleteReqDto> restAns = paramsEndpoint.requestDelete(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return getErrorText(translations.inBundle("common.Interface"));

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        ParamsDeleteData deleteData = registry.getOrCreate(userData, ParamsDeleteData::new);
        deleteData.setConfirm(fcd.getConfirm());
        deleteData.setDecline(fcd.getDecline());

        return getUserAlertMes(fcd, translations);
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, DeleteRequestMenu t) {
        ParamsDeleteData deleteData = registry.getOrCreate(userData, ParamsDeleteData::new);
        return switch (t) {
            case DELETE_CONFIRM -> {
                deleteData.setDecision(true);
                yield UserMenu.PARAMS_DELETE_STAGE_P;
            }
            case DELETE_DECLINE -> {
                deleteData.setDecision(false);
                yield UserMenu.PARAMS_DELETE_STAGE_P;
            }
        };
    }

    private String getUserAlertMes(FcdParamsDeleteReqDto fcd, LocalizedTexts translations) {
        return String.format("""
                        %s <b>%s</b>
                        
                        %s
                        %s <b>params-ID:</b> <code>%s</code>?
                        
                        <i>%s</i>
                        """,
                        DynamicEmoji.WARNING.getEmoji(),
                        translations.get("delete-parameters"),
                        translations.get("are-you-sure-you-want-to-delete"),
                        DynamicEmoji.ID.getEmoji(),
                        fcd.getTdpId(),
                        translations.format("important-b-the-set-of-parameters-will", Map.of("value1", String.format("<b>%s</b>", translations.get("currently-selected")), "emphasis", String.format("<b>%s</b>", translations.get("important-b-the-set-of-parameters-will.emphasis")))));
    }
}
