package cs.youtrade.autotrade.client.telegram.menu.start.user.token;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdTokenGetSingleDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserTokensState extends YTPTextMenuState<UserTokensMenu> {
    private final GeneralEndpoint endpoint;
    private final ParamsEndpoint paramsEndpoint;

    public UserTokensState(
            UserTextMessageSender sender,
            GeneralEndpoint endpoint,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN;
    }

    @Override
    public UserTokensMenu getOption(String optionStr) {
        return UserTokensMenu.valueOf(optionStr);
    }

    @Override
    public UserTokensMenu[] getOptions(UserData userData) {
        return UserTokensMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserTokensMenu t) {
        return switch (t) {
            case TOKEN_ADD -> UserMenu.TOKEN_ADD_STAGE_CHOOSE;
            case TOKEN_REMOVE -> UserMenu.TOKEN_REMOVE_STAGE_CHOOSE;
            case TOKEN_RENAME -> UserMenu.TOKEN_RENAME_STAGE_1;
            case RETURN -> UserMenu.USER;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        var restAns = endpoint.getTokens(chatId);
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var pathAns = paramsEndpoint.getCurrent(chatId);
        if (pathAns.getStatus() >= 300)
            return null;

        var pathFcd = pathAns.getResponse();
        if (!pathFcd.isResult())
            return pathFcd.getCause();

        var tokenListStr = getTokenListStr(fcd.getData());
        var pathData = pathFcd.getData();
        return String.format("""
                        %s <i>Управление аккаунтами</i>
                        
                        <blockquote expandable>%s</blockquote>
                        
                        %s
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                tokenListStr,
                pathData.getDirection()
        );
    }

    public String getTokenListStr(List<FcdTokenGetSingleDto> data) {
        if (data.isEmpty())
            return String.format("%s Список аккаунтов пуст",
                    DynamicEmoji.ERROR.getEmoji());

        return data
                .stream()
                .sorted(Comparator.comparingLong(FcdTokenGetSingleDto::getId))
                .map(FcdTokenGetSingleDto::asMessage)
                .collect(Collectors.joining("\n"))
                .trim();
    }
}
