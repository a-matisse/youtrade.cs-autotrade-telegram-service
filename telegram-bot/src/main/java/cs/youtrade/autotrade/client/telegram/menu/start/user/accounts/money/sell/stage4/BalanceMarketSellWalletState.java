package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage4;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.AbstractBalanceMarketSellTerminalState;
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
public class BalanceMarketSellWalletState extends AbstractBalanceMarketSellTerminalState {
    private static final String WALLET_PATTERN = "T[1-9A-HJ-NP-Za-km-z]{33}";

    public BalanceMarketSellWalletState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                        BalanceMarketSellText text) {
        super(sender, registry, text);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL_WALLET; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Отправьте полный адрес кошелька USDT TRC-20</b>
                """, text.summary(registry.getOrCreate(user, BalanceMarketSellData::new)), DynamicEmoji.WRITE.getEmoji());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!hasText(bot, update, user)) return supportedState();
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        String wallet = update.getMessage().getText().trim();
        if (!wallet.matches(WALLET_PATTERN)) {
            sender.sendTextMes(bot, user, "Проверьте адрес TRC-20: он должен начинаться с T и содержать 34 символа.");
            return supportedState();
        }
        registry.getOrCreate(user, BalanceMarketSellData::new).setWallet(wallet);
        return UserMenu.ACCOUNTS_SELL_PASSWORD;
    }
}
