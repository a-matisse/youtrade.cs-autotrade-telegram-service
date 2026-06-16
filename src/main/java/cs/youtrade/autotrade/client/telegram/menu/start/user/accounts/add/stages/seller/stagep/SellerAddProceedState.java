package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.seller.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.proceed.AbstractAddProceedState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellTokensAddEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Log4j2
public class SellerAddProceedState extends AbstractAddProceedState {
    private final UserApiRegistry registry;
    private final SellTokensAddEndpoint sellEndpoint;

    public SellerAddProceedState(
            UserTextMessageSender sender,
            UserApiRegistry registry,
            SellTokensAddEndpoint sellEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.sellEndpoint = sellEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_P_SELLER;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.remove(userData);
        var restAns = sellEndpoint.addToken(userData.getChatId(), data.getApi());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("%s <b>Аккаунт продажи успешно подключен! (Подключенный ключ: <tg-spoiler>\"%s\"</tg-spoiler>)</b>",
                DynamicEmoji.SUCCESS.getEmoji(),
                fcd.getVisible() + fcd.getHidden()
        );
    }
}
