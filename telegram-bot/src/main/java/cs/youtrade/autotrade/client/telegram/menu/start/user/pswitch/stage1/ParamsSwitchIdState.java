package cs.youtrade.autotrade.client.telegram.menu.start.user.pswitch.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.pswitch.ParamsSwitchData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.pswitch.ParamsSwitchRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsListDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class ParamsSwitchIdState extends YTPTextState {
    private final ParamsSwitchRegistry registry;
    private final ParamsEndpoint endpoint;

    public ParamsSwitchIdState(
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
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsSwitch");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote expandable>%s</blockquote>
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("enter-a-params-id-to-switch-to"),
                        getParamsStr(userData));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_SWITCH_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.ParamsSwitch");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-returning-to-the-menu")));
            return UserMenu.USER;
        }

        String input = update.getMessage().getText();
        var data = registry.getOrCreate(user, ParamsSwitchData::new);
        data.setInput(input);
        return UserMenu.PARAMS_SWITCH_STAGE_P;
    }

    private String getParamsStr(UserData user) {
        var translations = botTexts.forUser(user, "parameters.ParamsSwitch");
        var restAns = endpoint.listParams(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = fcd.getData();
        if (data.isEmpty())
            return translations.get("the-scoring-id-list-is-empty");

        return data
                .stream()
                .sorted(Comparator.comparingLong(FcdParamsListDto::getTdpId))
                .map(value -> value.asMessage(translations.inBundle("summary.ParamsSummary")))
                .collect(Collectors.joining("\n"));
    }
}
