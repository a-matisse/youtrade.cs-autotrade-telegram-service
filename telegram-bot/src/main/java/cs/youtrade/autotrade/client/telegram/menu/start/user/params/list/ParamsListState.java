package cs.youtrade.autotrade.client.telegram.menu.start.user.params.list;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsListDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParamsListState extends YTPTerminalTextMenuState {
    private final ParamsEndpoint endpoint;

    public ParamsListState(
            UserTextMessageSender sender,
            ParamsEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_LIST;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsList");
        var restAns = endpoint.listParams(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return String.format("""
                        📋 <b>%s</b>
                        ━━━━━━━━━━━━━━
                        <blockquote expandable>%s</blockquote>
                        """,
                        translations.get("your-params-id-list"),
                        getStringFromFcd(fcd, translations));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PARAMS;
    }

    private String getStringFromFcd(FcdDefaultDto<List<FcdParamsListDto>> fcd, LocalizedTexts translations) {
        return fcd
                .getData()
                .stream()
                .map(value -> value.asMessage(translations.inBundle("summary.ParamsSummary")))
                .collect(Collectors.joining("\n"));
    }
}
