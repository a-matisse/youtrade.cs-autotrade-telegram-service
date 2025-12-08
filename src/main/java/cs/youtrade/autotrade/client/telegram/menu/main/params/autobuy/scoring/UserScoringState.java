package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractPcoTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetScoringDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserScoringState extends AbstractPcoTextMenuState<UserScoringMenu> {
    private final ParamsEndpoint endpoint;

    public UserScoringState(
            UserTextMessageSender sender,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING;
    }

    @Override
    public UserScoringMenu getOption(String optionStr) {
        return UserScoringMenu.valueOf(optionStr);
    }

    @Override
    public UserScoringMenu[] getOptions() {
        return UserScoringMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserScoringMenu t) {
        return switch (t) {
            case SCORING_ADD -> UserMenu.SCORING_ADD_STAGE_1;
            case SCORING_EDIT -> UserMenu.SCORING_EDIT_STAGE_1;
            case SCORING_REMOVE -> UserMenu.SCORING_REMOVE_STAGE_1;
            case RETURN -> UserMenu.AUTOBUY;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var restAns = endpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("""
                        Имя: %s
                        🆔 params-ID=%s
                        
                        %s
                        %s
                        """,
                fcd.getData().getGivenName(),
                fcd.getData().getTdpId(),
                getScoringStr(fcd.getData()),
                getFollowWorks(fcd.getData())
        );
    }

    private String getScoringStr(FcdParamsGetDto tdp) {
        return tdp
                .getScoringData()
                .stream()
                .map(FcdParamsGetScoringDto::asMessage)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public List<ParamsCopyOptions> getMenuPcos() {
        return List.of(ParamsCopyOptions.SCORING);
    }
}
