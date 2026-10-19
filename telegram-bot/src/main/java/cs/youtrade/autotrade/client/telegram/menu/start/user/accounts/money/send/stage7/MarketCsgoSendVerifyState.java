package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage7;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoAccountLookup;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendTransfer;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage6.MarketCsgoSendProcessingState;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoSendVerifyState extends AbstractMarketCsgoSendState<MarketCsgoSendVerifyMenu> {
    private final MarketCsgoAccountLookup lookup;
    private final MarketCsgoSendTransfer transfer;
    private final MarketCsgoSendProcessingState processingState;

    public MarketCsgoSendVerifyState(UserTextMessageSender sender, MarketCsgoSendRegistry registry,
                                     MarketCsgoAccountLookup lookup, MarketCsgoSendTransfer transfer,
                                     MarketCsgoSendProcessingState processingState, BotTexts botTexts) {
        super(sender, registry, botTexts);
        this.lookup = lookup;
        this.transfer = transfer;
        this.processingState = processingState;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_VERIFY; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSendVerify");
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        String balance = data.getCurrentBalance() == null ? "" : String.format("""

                🔄 <b>%s: $%s USD</b>
                """,
                translations.get("available-now"),
                money(data.getCurrentBalance()));
        return String.format("""
                %s%s <b>%s</b>
                <blockquote>%s</blockquote>
                %s
                """,
                summary(user),
                DynamicEmoji.WARNING.getEmoji(),
                translations.format("test-transfer-of-sent", Map.of("argument3", String.valueOf(transfer.testAmount().toPlainString()))),
                translations.get("this-is-a-required-security-check-make"),
                balance);
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, MarketCsgoSendVerifyMenu option) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSendVerify");
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        synchronized (data) {
            if (data.isProcessing()) return UserMenu.ACCOUNTS_SEND_PROCESSING;
            if (data.getResult() != null) return UserMenu.ACCOUNTS_SEND_RESULT;
            if (option == MarketCsgoSendVerifyMenu.REFRESH) {
                try {
                    var account = lookup.find(user.getChatId(), data.getTokenId());
                    if (account == null || account.available() == null)
                        throw new IllegalStateException("Balance unavailable");
                    data.setCurrentBalance(account.available());
                } catch (RuntimeException error) {
                    sender.sendTextMes(bot, user, translations.get("could-not-refresh-the-balance-try-again"));
                }
                return supportedState();
            }
            if (option == MarketCsgoSendVerifyMenu.NOT_RECEIVED) {
                data.setResult(MarketCsgoSendData.Result.NOT_RECEIVED);
                data.eraseSecrets();
                return UserMenu.ACCOUNTS_SEND_RESULT;
            }
            if (option != MarketCsgoSendVerifyMenu.RECEIVED) return supportedState();
            if (data.isRemainderAttempted()) return UserMenu.ACCOUNTS_SEND_RESULT;
            data.setRemainderAttempted(true);
            data.setProcessing(true);
            processingState.show(bot, user);
            try {
                transfer.sendRemaining(user, data);
                return UserMenu.ACCOUNTS_SEND_RESULT;
            } finally {
                data.setProcessing(false);
            }
        }
    }

    @Override
    public MarketCsgoSendVerifyMenu getOption(String value) {
        return MarketCsgoSendVerifyMenu.valueOf(value);
    }

    @Override
    public MarketCsgoSendVerifyMenu[] getOptions(UserData user) {
        return MarketCsgoSendVerifyMenu.values();
    }
}
