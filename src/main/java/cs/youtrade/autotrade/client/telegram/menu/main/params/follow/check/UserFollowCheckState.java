package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.check;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserFollowCheckState extends AbstractTextMenuState<UserFollowCheckMenu> {
    private final UserFollowCheckRegistry registry;
    private final ParamsEndpoint endpoint;

    public UserFollowCheckState(
            UserTextMessageSender sender,
            UserFollowCheckRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
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
            case ACCEPT -> onAccept(bot, userData);
            case DENY -> onDeny(bot, userData);
            case RETURN -> UserMenu.FOLLOW;
        };
    }

    @Override
    public String getHeaderText(UserData user) {
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

    private UserMenu onAccept(TelegramClient bot, UserData userData) {
        var data = registry.remove(userData);
        var callbackDto = data.getDto().getCallbackDto();
        var restAns = switch (data.getType()) {
            case FOLLOW -> endpoint.proceedFollow(callbackDto.getConfirm());
            case COPY -> endpoint.proceedCopy(callbackDto.getConfirm());
        };
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        sendBoth(bot, fcd, getError(fcd));
        return UserMenu.FOLLOW_CHECK;
    }

    private UserMenu onDeny(TelegramClient bot, UserData userData) {
        var data = registry.remove(userData);
        var callbackDto = data.getDto().getCallbackDto();
        var restAns = switch (data.getType()) {
            case FOLLOW -> endpoint.proceedFollow(callbackDto.getDecline());
            case COPY -> endpoint.proceedCopy(callbackDto.getDecline());
        };
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        sendBoth(bot, fcd, getSuccessFollow(fcd));
        return UserMenu.FOLLOW_CHECK;
    }

    private void sendBoth(TelegramClient bot, FcdParamsCopyResDto fcd, String mes) {
        sender.sendTextMes(bot, fcd.getThatChatId(), mes);
        sender.sendTextMes(bot, fcd.getYourChatId(), mes);
    }

    private String getSuccessFollow(FcdParamsCopyResDto fcd) {
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("✅ Активировано следование: params-ID=%d → params-ID=%d",
                fcd.getThatTdpId(), fcd.getYourTdpId());
    }

    private String getError(FcdParamsCopyResDto fcd) {
        return fcd.getCause();
    }
}
