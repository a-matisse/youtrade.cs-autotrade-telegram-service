package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage3;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.AbstractBalanceMarketSellState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellText;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class BalanceMarketSellMethodState extends AbstractBalanceMarketSellState<BalanceMarketSellMethodMenu> {
    public BalanceMarketSellMethodState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                        BalanceMarketSellText text) {
        super(sender, registry, text);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL_METHOD; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Выберите способ получения</b>
                """, text.summary(registry.getOrCreate(user, BalanceMarketSellData::new)), DynamicEmoji.QUESTION.getEmoji());
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, BalanceMarketSellMethodMenu option) {
        if (option == BalanceMarketSellMethodMenu.RETURN) return leave(user);
        if (option == BalanceMarketSellMethodMenu.USDT_TRC20) {
            registry.getOrCreate(user, BalanceMarketSellData::new).setMethod("USDT_TRC20");
            return UserMenu.ACCOUNTS_SELL_WALLET;
        }
        return supportedState();
    }

    @Override
    public BalanceMarketSellMethodMenu getOption(String value) {
        return BalanceMarketSellMethodMenu.valueOf(value);
    }

    @Override
    public BalanceMarketSellMethodMenu[] getOptions(UserData user) {
        return BalanceMarketSellMethodMenu.values();
    }
}
