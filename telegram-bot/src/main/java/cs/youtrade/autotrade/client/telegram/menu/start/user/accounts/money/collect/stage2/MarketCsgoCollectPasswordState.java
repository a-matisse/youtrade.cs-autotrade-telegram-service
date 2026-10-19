package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stage3.MarketCsgoCollectProcessingState;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.MarketCsgoBalanceEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdMarketCsgoBalanceTransferInput;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoCollectPasswordState extends YTPTerminalTextMenuState {
    private final MarketCsgoCollectRegistry registry;
    private final MarketCsgoBalanceEndpoint endpoint;
    private final MarketCsgoCollectProcessingState processingState;

    public MarketCsgoCollectPasswordState(UserTextMessageSender sender, MarketCsgoCollectRegistry registry,
                                          MarketCsgoBalanceEndpoint endpoint,
                                          MarketCsgoCollectProcessingState processingState, BotTexts botTexts) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
        this.processingState = processingState;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_COLLECT_PASSWORD; }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TerminalMenu option) {
        var data = registry.getOrCreate(user, MarketCsgoCollectData::new);
        if (data.isProcessing()) return UserMenu.ACCOUNTS_COLLECT_PROCESSING;
        if (data.isCompleted()) return UserMenu.ACCOUNTS_COLLECT_RESULT;
        registry.get(user);
        return UserMenu.ACCOUNTS;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-collect.MarketCsgoCollectPassword");
        return String.format("""
                %s <i>%s</i>

                %s <b>%s</b>
                <blockquote>• %s: <code>%s</code>
                • %s</blockquote>

                %s <b>%s</b>
                <blockquote>%s</blockquote>
                """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                translations.get("collect-market-csgo-balances"),
                DynamicEmoji.MONEY.getEmoji(),
                translations.get("recipient"),
                translations.get("account"),
                registry.getOrCreate(user, MarketCsgoCollectData::new).getTokenId(),
                translations.get("password-awaiting-input"),
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("send-your-market-csgo-payment-password"),
                translations.get("all-accounts-must-use-the-same-password"));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-collect.MarketCsgoCollectPassword");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        var data = registry.getOrCreate(user, MarketCsgoCollectData::new);
        synchronized (data) {
            if (data.isCompleted())
                return UserMenu.ACCOUNTS_COLLECT_RESULT;
            if (data.isProcessing())
                return UserMenu.ACCOUNTS_COLLECT_PROCESSING;
            sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
            String password = update.getMessage().getText().trim();
            if (password.isEmpty()) {
                sender.sendTextMes(bot, user, translations.get("the-payment-password-cannot-be-empty"));
                return supportedState();
            }
            data.setProcessing(true);
            processingState.show(bot, user);
            FcdMarketCsgoBalanceTransferInput input = new FcdMarketCsgoBalanceTransferInput();
            input.setDestinationTokenId(data.getTokenId());
            input.setPaymentPassword(password);
            try {
                var answer = endpoint.balanceTransfer(user.getChatId(), input);
                var response = answer.getResponse();
                data.setResponse(answer.getStatus() >= 300
                        && (response == null || response.isResult()) ? null : response);
            } catch (RuntimeException error) {
                data.setResponse(null);
            } finally {
                input.setPaymentPassword(null);
                data.setCompleted(true);
                data.setProcessing(false);
            }
            return UserMenu.ACCOUNTS_COLLECT_RESULT;
        }
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }
}
