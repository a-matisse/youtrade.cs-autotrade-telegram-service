package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.follow.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.check.UserFollowCheckData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.check.UserFollowCheckRegistry;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.follow.UserFollowData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.follow.UserFollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class FollowProceedState extends AbstractTerminalTextMenuState {
    private final UserFollowRegistry registry;
    private final UserFollowCheckRegistry checkRegistry;
    private final ParamsEndpoint endpoint;

    public FollowProceedState(
            UserTextMessageSender sender,
            UserFollowRegistry registry,
            UserFollowCheckRegistry checkRegistry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.checkRegistry = checkRegistry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        var data = registry.remove(user);
        var restAns = switch (data.getType()) {
            case FOLLOW -> endpoint.requestFollow(chatId, data.getYourTdpId(), data.getPco());
            case COPY -> endpoint.requestCopy(chatId, data.getYourTdpId(), data.getPco());
        };
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        long yourChatId = fcd.getYourChatId();
        UserData yourUserData = new UserData(yourChatId);
        checkRegistry.getOrCreate(yourUserData, () -> new UserFollowCheckData(data.getType(), fcd, data.getPco()));
        return genMes(data, fcd);
    }

    @Override
    public UserMenu retState() {
        return UserMenu.FOLLOW;
    }

    private String genMes(UserFollowData data, FcdParamsCopyReqDto copyDto) {
        return switch (data.getType()) {
            case FOLLOW -> followMes(copyDto, data.getPco());
            case COPY -> copyMes(copyDto, data.getPco());
        };
    }

    public String followMes(
            FcdParamsCopyReqDto copyDto,
            ParamsCopyOptions pco
    ) {
        return String.format("Запрос на следование (%s) за параметрами [%s] отправлен",
                pco.getModeName(), copyDto.getYourTdpName());
    }

    public String copyMes(
            FcdParamsCopyReqDto copyDto,
            ParamsCopyOptions pco
    ) {
        return String.format("Запрос на копирование (%s) параметров [%s] отправлен",
                pco.getModeName(), copyDto.getYourTdpName());
    }
}
