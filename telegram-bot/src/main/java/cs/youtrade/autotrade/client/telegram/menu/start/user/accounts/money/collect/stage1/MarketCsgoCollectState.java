package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoAccountLookup;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoCollectState extends YTPTerminalTextMenuState {
    private final MarketCsgoAccountLookup lookup;
    private final MarketCsgoCollectRegistry registry;

    public MarketCsgoCollectState(UserTextMessageSender sender, MarketCsgoAccountLookup lookup,
                                  MarketCsgoCollectRegistry registry, BotTexts botTexts) {
        super(sender, botTexts);
        this.lookup = lookup;
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_COLLECT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-collect.MarketCsgoCollect");
        try {
            if (!lookup.isMarketCsgo(user.getChatId()))
                return translations.get("balance-transfers-are-only-available-for-the");
            return String.format("""
                    %s <i>%s</i>

                    %s <b>%s</b>
                    <blockquote>• %s: —
                    • %s: —</blockquote>

                    %s <b>%s</b>
                    <blockquote expandable>%s</blockquote>
                    """,
                    DynamicEmoji.YOUTRADE.getEmoji(),
                    translations.get("collect-market-csgo-balances"),
                    DynamicEmoji.MONEY.getEmoji(),
                    translations.get("recipient"),
                    translations.get("account"),
                    translations.get("password"),
                    DynamicEmoji.WRITE.getEmoji(),
                    translations.get("send-the-id-of-the-account-to"),
                    lookup.currentPageBalances(user.getChatId(), translations.inBundle("accounts.MoneyCommon")));
        } catch (RuntimeException error) {
            return translations.get("could-not-load-accounts-try-again");
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-collect.MarketCsgoCollect");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        try {
            if (!lookup.isMarketCsgo(user.getChatId())) {
                sender.sendTextMes(bot, user, translations.get("balance-transfers-are-only-available-for-the"));
                return supportedState();
            }
            long id = Long.parseLong(update.getMessage().getText().trim());
            var account = lookup.find(user.getChatId(), id);
            if (account == null || account.available() == null) {
                sender.sendTextMes(bot, user, translations.get("no-market-csgo-selling-account-found-in"));
                return supportedState();
            }
            registry.getOrCreate(user, MarketCsgoCollectData::new).setTokenId(id);
            return UserMenu.ACCOUNTS_COLLECT_PASSWORD;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, translations.get("enter-one-account-id-as-a-number"));
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, translations.get("could-not-check-the-account-try-again"));
        }
        return supportedState();
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }
}
