package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoAccountLookup;
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
public class BalanceMarketSellState extends AbstractBalanceMarketSellTerminalState {
    private final MarketCsgoAccountLookup lookup;

    public BalanceMarketSellState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                  BalanceMarketSellText text, MarketCsgoAccountLookup lookup, BotTexts botTexts) {
        super(sender, registry, text, botTexts);
        this.lookup = lookup;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSell");
        String summary = text.summary(registry.getOrCreate(user, BalanceMarketSellData::new), translations.inBundle("accounts.money-sell.BalanceMarketSellCommon"));
        try {
            if (!lookup.isMarketCsgo(user.getChatId()))
                return String.format("""
                        %s%s
                        """,
                        summary,
                        translations.get("balance-sales-are-only-available-for-the"));
            return String.format("""
                    %s%s <b>%s</b>
                    <blockquote expandable>%s</blockquote>
                    """,
                    summary,
                    DynamicEmoji.WRITE.getEmoji(),
                    translations.get("send-the-id-of-the-account-whose"),
                    lookup.currentPageBalances(user.getChatId(), translations.inBundle("accounts.MoneyCommon")));
        } catch (RuntimeException error) {
            return String.format("""
                        %s%s
                        """,
                        summary,
                        translations.get("could-not-load-accounts-try-again"));
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSell");
        if (!hasText(bot, update, user)) return supportedState();
        try {
            if (!lookup.isMarketCsgo(user.getChatId())) {
                sender.sendTextMes(bot, user, translations.get("balance-sales-are-only-available-for-the"));
                return supportedState();
            }
            long id = Long.parseLong(update.getMessage().getText().trim());
            var account = lookup.find(user.getChatId(), id);
            if (account == null || account.available() == null) {
                sender.sendTextMes(bot, user, translations.get("no-market-csgo-selling-account-found-in"));
                return supportedState();
            }
            if (account.available().signum() <= 0) {
                sender.sendTextMes(bot, user, translations.get("there-is-nothing-to-sell-from-this"));
                return supportedState();
            }
            var data = registry.getOrCreate(user, BalanceMarketSellData::new);
            data.setAccountId(id);
            data.setAvailable(account.available());
            return UserMenu.ACCOUNTS_SELL_AMOUNT;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, translations.get("enter-one-account-id-as-a-number"));
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, translations.get("could-not-check-the-account-try-again"));
        }
        return supportedState();
    }
}
