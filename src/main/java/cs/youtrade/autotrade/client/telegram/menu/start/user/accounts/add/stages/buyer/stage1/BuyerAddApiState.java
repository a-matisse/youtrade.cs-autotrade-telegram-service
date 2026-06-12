package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.buyer.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.api.AbstractAddApiState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class BuyerAddApiState extends AbstractAddApiState {
    public BuyerAddApiState(
            UserTextMessageSender sender,
            UserApiRegistry registry
    ) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_1_BUYER;
    }

    @Override
    public UserMenu getNextState(TelegramClient bot, Update update, UserData userData) {
        return UserMenu.ACCOUNTS_ADD_STAGE_2_BUYER;
    }

    @Override
    public MarketType getMarketType(UserApiData apiData) {
        return apiData.getSource();
    }
}
