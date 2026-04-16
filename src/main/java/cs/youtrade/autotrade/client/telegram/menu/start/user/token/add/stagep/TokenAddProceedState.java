package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellTokensAddEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TokenAddProceedState extends YTPTerminalTextMenuState {
    private final UserTokenAddRegistry registry;
    private final BuyEndpoint buyEndpoint;
    private final SellTokensAddEndpoint sellEndpoint;

    public TokenAddProceedState(
            UserTextMessageSender sender,
            UserTokenAddRegistry registry,
            BuyEndpoint buyEndpoint,
            SellTokensAddEndpoint sellEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.buyEndpoint = buyEndpoint;
        this.sellEndpoint = sellEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_ADD_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        return switch (data.getOpt()) {
            case BUY_TOKEN -> buyTokenAddAns(user, data);
            case SELL_TOKEN -> sellTokenAddAns(user, data);
            case RETURN -> "Возврат в меню аккаунтов...";
        };
    }

    @Override
    public UserMenu retState() {
        return UserMenu.TOKEN;
    }

    private String buyTokenAddAns(UserData user, UserTokenAddData data) {
        var restAns = buyEndpoint.buyTokensAdd(user.getChatId(), data.getApi(), data.getPartnerId(), data.getSteamToken());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format(
                "✅ <b>Новый аккаунт (Трейд-ссылка заканчивается на <tg-spoiler>\"%s\"</tg-spoiler>) добавлен успешно!</b>",
                data.getSteamToken()
        );
    }

    private String sellTokenAddAns(UserData user, UserTokenAddData data) {
        var restAns = sellEndpoint.addToken(user.getChatId(), data.getApi());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return "✅ <b>API-ключ продажи добавлен успешно! (<tg-spoiler>" + fcd.getVisible() + fcd.getHidden() + "</tg-spoiler>)</b>";
    }
}
