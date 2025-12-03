package cs.youtrade.autotrade.client.telegram.menu.main.params.token.get;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdTokenGetSingleDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class UserTokenGetState extends AbstractTerminalTextMenuState {
    private final GeneralEndpoint endpoint;

    public UserTokenGetState(
            UserTextMessageSender sender,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_GET;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = endpoint.getTokens(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return fcd
                .getData()
                .stream()
                .map(FcdTokenGetSingleDto::asMessage)
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.TOKEN;
    }
}
