package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractBalanceMarketSellTerminalState extends YTPTerminalTextMenuState {
    protected final BalanceMarketSellRegistry registry;
    protected final BalanceMarketSellText text;

    protected AbstractBalanceMarketSellTerminalState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                             BalanceMarketSellText text, BotTexts botTexts) {
        super(sender, botTexts);
        this.registry = registry;
        this.text = text;
    }

    protected boolean hasText(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellCommon");
        if (update.hasMessage() && update.getMessage().hasText()) return true;
        sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
        return false;
    }

    protected UserMenu leave(UserData user) {
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        if (data.isProcessing()) return UserMenu.ACCOUNTS_SELL_PROCESSING;
        registry.get(user);
        data.eraseSecrets();
        return UserMenu.ACCOUNTS;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TerminalMenu option) {
        return leave(user);
    }
}
