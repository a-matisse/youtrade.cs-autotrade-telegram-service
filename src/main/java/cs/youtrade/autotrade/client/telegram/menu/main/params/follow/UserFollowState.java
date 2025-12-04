package cs.youtrade.autotrade.client.telegram.menu.main.params.follow;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsFollowDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class UserFollowState extends AbstractTextMenuState<UserFollowMenu> {
    private final ParamsEndpoint endpoint;

    public UserFollowState(
            UserTextMessageSender sender,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW;
    }

    @Override
    public UserFollowMenu getOption(String optionStr) {
        return UserFollowMenu.valueOf(optionStr);
    }

    @Override
    public UserFollowMenu[] getOptions() {
        return UserFollowMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserFollowMenu t) {
        return switch (t) {
            case FOLLOW_CHECK -> UserMenu.FOLLOW_CHECK;
            case FOLLOW_FOLLOW -> UserMenu.FOLLOW_STAGE_CHOOSE;
            case FOLLOW_UNFOLLOW -> UserMenu.FOLLOW_UNFOLLOW_STAGE_1;
            case RETURN -> UserMenu.PARAMS;
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

        return getFollowInfo(fcd.getData());
    }

    private String getFollowInfo(FcdParamsGetDto fcd) {
        return String.format("""
                        Текущие следования (follow-ID):
                        %s
                        """,
                getFollowStr(fcd)
        );
    }

    private String getFollowStr(FcdParamsGetDto fcd) {
        if (fcd.getFollows() == null || fcd.getFollows().isEmpty())
            return "🔴 Не работает";

        String ans = fcd
                .getFollows()
                .stream()
                .map(FcdParamsFollowDto::asMessage)
                .collect(Collectors.joining("\n"));

        return String.format("""
                        params-ID=%s
                        Имя: %s
                        
                        🟢 Следование работает
                        
                        %s
                        """,
                fcd.getTdpId(),
                fcd.getGivenName(),
                ans
        );
    }
}
