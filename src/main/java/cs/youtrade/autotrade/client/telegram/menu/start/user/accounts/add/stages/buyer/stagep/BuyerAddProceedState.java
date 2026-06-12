package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.buyer.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.proceed.AbstractAddProceedState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class BuyerAddProceedState extends AbstractAddProceedState {
    private final UserApiRegistry registry;
    private final BuyEndpoint buyEndpoint;

    public BuyerAddProceedState(
            UserTextMessageSender sender,
            UserApiRegistry registry,
            BuyEndpoint buyEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.buyEndpoint = buyEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_P_BUYER;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.remove(userData);
        var restAns = buyEndpoint.buyTokensAdd(userData.getChatId(), data.getApi(), data.getPartnerId(), data.getSteamToken());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("%s <b>Аккаунт покупки успешно подключен! (Trade-токен пользователя: <tg-spoiler>\"%s\"</tg-spoiler>)</b>",
                DynamicEmoji.SUCCESS.getEmoji(),
                data.getSteamToken()
        );
    }
}
