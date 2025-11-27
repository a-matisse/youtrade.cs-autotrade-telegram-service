package cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import org.springframework.stereotype.Service;

@Service
public class TokenDeleteProceedState extends AbstractTerminalTextMenuState {
    private final UserTokenDeleteRegistry registry;
    private final BuyEndpoint endpoint;

    public TokenDeleteProceedState(
            UserTextMessageSender sender,
            UserTokenDeleteRegistry registry,
            BuyEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_REMOVE_STAGE_P;
    }

    @Override
    public String getHeaderText(UserData user) {
        var data = registry.remove(user);
        var restAns = switch (data.getOpt()) {
            case SINGLE -> endpoint.tokenDelete(user.getChatId(), data.getTokenId());
            case ALL -> endpoint.tokenDeleteAll(user.getChatId());
        };
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("Успешно удалено %s токенов", fcd.getData());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.TOKEN;
    }
}
