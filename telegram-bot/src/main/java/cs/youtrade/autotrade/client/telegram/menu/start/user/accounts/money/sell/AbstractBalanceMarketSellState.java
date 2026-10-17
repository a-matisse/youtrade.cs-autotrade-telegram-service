package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell;

import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractBalanceMarketSellState<MENU extends IMenuEnum> extends YTPTextMenuState<MENU> {
    protected final BalanceMarketSellRegistry registry;
    protected final BalanceMarketSellText text;

    protected AbstractBalanceMarketSellState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                             BalanceMarketSellText text) {
        super(sender);
        this.registry = registry;
        this.text = text;
    }

    protected UserMenu restart(UserData user) {
        var previous = registry.get(user);
        if (previous != null) previous.eraseSecrets();
        return UserMenu.ACCOUNTS_SELL;
    }

    protected boolean hasText(TelegramClient bot, Update update, UserData user) {
        if (update.hasMessage() && update.getMessage().hasText()) return true;
        sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
        return false;
    }

    protected UserMenu leave(UserData user) {
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        if (data.isProcessing()) return UserMenu.ACCOUNTS_SELL_PROCESSING;
        registry.get(user);
        data.eraseSecrets();
        return UserMenu.ACCOUNTS;
    }
}
