package cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserRenameData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserParamsRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsListDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParamsRenameIdState extends YTPTextState {
    private final UserParamsRenameRegistry registry;
    private final ParamsEndpoint endpoint;

    public ParamsRenameIdState(
            UserTextMessageSender sender,
            UserParamsRenameRegistry registry,
            ParamsEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsRename");
        return String.format("""
                        📋 <b>%s</b>
                        ━━━━━━━━━━━━━━
                        <blockquote expandable>%s</blockquote>
                        %s
                        """,
                        translations.get("your-params-id-list"),
                        getParamsStr(userData),
                        translations.format("enter-a-params-id-b-to-rename", Map.of("emphasis", String.format("<b>%s</b>", translations.get("enter-a-params-id-b-to-rename.emphasis")))));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_RENAME_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.ParamsRename");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.PARAMS;
        }

        String input = update.getMessage().getText();
        long paramsId;
        try {
            paramsId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                    translations.get("the-value-entered-is-not-a-number"),
                    input));
            return UserMenu.PARAMS;
        }

        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setId(paramsId);
        return UserMenu.PARAMS_RENAME_STAGE_2;
    }

    private String getParamsStr(UserData user) {
        var translations = botTexts.forUser(user, "parameters.ParamsRename");
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
                .map(value -> value.asMessage(translations.inBundle("summary.ParamsSummary")))
                .collect(Collectors.joining("\n"));
    }
}
