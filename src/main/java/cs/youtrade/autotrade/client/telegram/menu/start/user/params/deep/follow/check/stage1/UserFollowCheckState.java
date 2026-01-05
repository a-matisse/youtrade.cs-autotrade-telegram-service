package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.follow.check.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.follow.check.UserFollowCheckData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.follow.check.UserFollowCheckRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqDto;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserFollowCheckState extends AbstractTextMenuState<UserFollowCheckMenu> {
    private final UserFollowCheckRegistry registry;

    public UserFollowCheckState(
            UserTextMessageSender sender,
            UserFollowCheckRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_CHECK;
    }

    @Override
    public UserFollowCheckMenu getOption(String optionStr) {
        return UserFollowCheckMenu.valueOf(optionStr);
    }

    @Override
    public UserFollowCheckMenu[] getOptions() {
        return UserFollowCheckMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserFollowCheckMenu t) {
        return switch (t) {
            case ACCEPT -> UserMenu.FOLLOW_CHECK_ACCEPT;
            case DENY -> UserMenu.FOLLOW_CHECK_DENY;
            case RETURN -> UserMenu.FOLLOW;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.get(user);
        if (data == null)
            return onEmptyMes();

        return genMes(data);
    }

    private String genMes(UserFollowCheckData dto) {
        return switch (dto.getType()) {
            case FOLLOW -> followMes(dto.getDto(), dto.getPco());
            case COPY -> copyMes(dto.getDto(), dto.getPco());
        };
    }

    private String copyMes(
            FcdParamsCopyReqDto copyDto,
            ParamsCopyOptions pco
    ) {
        return String.format("Пользователь %s отправил запрос на копирование (%s) параметров [%s]",
                copyDto.getThatUId(), pco.getModeName(), copyDto.getYourTdpName());
    }

    private String followMes(
            FcdParamsCopyReqDto copyDto,
            ParamsCopyOptions pco
    ) {
        return String.format("Пользователь %s запросил следование (%s) за вашими параметрами [%s]",
                copyDto.getThatUId(), pco.getModeName(), copyDto.getYourTdpName());
    }

    private String onEmptyMes() {
        return "⏳ Заявки отсутствуют, попробуйте позже";
    }
}
