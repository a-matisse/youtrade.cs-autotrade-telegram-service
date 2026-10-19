package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoAccountLookup;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendTerminalState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendTransfer;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoSendState extends AbstractMarketCsgoSendTerminalState {
    private final MarketCsgoAccountLookup lookup;
    private final MarketCsgoSendTransfer transfer;

    public MarketCsgoSendState(UserTextMessageSender sender, MarketCsgoSendRegistry registry,
                               MarketCsgoAccountLookup lookup, MarketCsgoSendTransfer transfer, BotTexts botTexts) {
        super(sender, registry, botTexts);
        this.lookup = lookup;
        this.transfer = transfer;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSend");
        try {
            if (!lookup.isMarketCsgo(user.getChatId()))
                return translations.get("withdrawals-are-only-available-for-the-market");
            return String.format("""
                    %s%s <b>%s</b>
                    <blockquote expandable>%s</blockquote>
                    """,
                    summary(user),
                    DynamicEmoji.WRITE.getEmoji(),
                    translations.get("send-the-id-of-the-account-to"),
                    lookup.currentPageBalances(user.getChatId(), translations.inBundle("accounts.MoneyCommon")));
        } catch (RuntimeException error) {
            return translations.get("could-not-check-the-trading-route-try");
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSend");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        try {
            if (!lookup.isMarketCsgo(user.getChatId())) {
                sender.sendTextMes(bot, user, translations.get("withdrawals-are-only-available-for-the-market"));
                return supportedState();
            }
            long id = Long.parseLong(update.getMessage().getText().trim());
            var account = lookup.find(user.getChatId(), id);
            if (account == null || account.available() == null) {
                sender.sendTextMes(bot, user, translations.get("no-market-csgo-selling-account-found-in"));
                return supportedState();
            }
            if (account.available().signum() <= 0) {
                sender.sendTextMes(bot, user, translations.get("there-is-nothing-to-transfer-from-this"));
                return supportedState();
            }
            if (account.available().compareTo(transfer.testAmount()) < 0) {
                sender.sendTextMes(bot, user, translations.get("insufficient-balance-for-a-test-transfer-of")
                        + transfer.testAmount().toPlainString() + ".");
                return supportedState();
            }
            var data = registry.getOrCreate(user, MarketCsgoSendData::new);
            data.setTokenId(id);
            data.setAvailable(account.available());
            return UserMenu.ACCOUNTS_SEND_PASSWORD;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, translations.get("enter-one-account-id-as-a-number"));
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, translations.get("could-not-check-the-account-try-again"));
        }
        return supportedState();
    }
}
