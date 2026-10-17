package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send;

import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;

import java.math.BigDecimal;

public abstract class AbstractMarketCsgoSendState<MENU extends IMenuEnum> extends YTPTextMenuState<MENU> {
    protected final MarketCsgoSendRegistry registry;

    protected AbstractMarketCsgoSendState(UserTextMessageSender sender, MarketCsgoSendRegistry registry) {
        super(sender);
        this.registry = registry;
    }

    protected String summary(UserData user) {
        return MarketCsgoSendText.summary(registry.getOrCreate(user, MarketCsgoSendData::new));
    }

    protected static String money(BigDecimal value) {
        return MarketCsgoSendText.money(value);
    }

    protected UserMenu leave(UserData user) {
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        if (data.isProcessing()) return UserMenu.ACCOUNTS_SEND_PROCESSING;
        registry.get(user);
        data.eraseSecrets();
        return UserMenu.ACCOUNTS;
    }
}
