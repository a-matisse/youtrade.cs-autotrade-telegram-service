package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage6;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.AbstractBalanceMarketSellState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellText;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage7.BalanceMarketSellProcessingState;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.BalanceMarketDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.BalanceMarketEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.UUID;

@Service
@Log4j2
public class BalanceMarketSellReviewState extends AbstractBalanceMarketSellState<BalanceMarketSellReviewMenu> {
    private final BalanceMarketEndpoint endpoint;
    private final BalanceMarketSellProcessingState processingState;

    public BalanceMarketSellReviewState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                        BalanceMarketSellText text, BalanceMarketEndpoint endpoint,
                                        BalanceMarketSellProcessingState processingState, BotTexts botTexts) {
        super(sender, registry, text, botTexts);
        this.endpoint = endpoint;
        this.processingState = processingState;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL_REVIEW; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellReview");
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        if (data.getQuote() == null) return String.format("""
                %s%s
                """,
                text.summary(data, translations.inBundle("accounts.money-sell.BalanceMarketSellCommon")),
                translations.get("the-quote-has-expired-tap-confirm-to"));
        return String.format("""
                %s%s <b>%s</b>
                <blockquote>• %s: <b>TRC-20</b>
                • %s: <code>%s</code></blockquote>

                %s└ <i>%s
                %s</i>

                %s
                """,
                text.summary(data, translations.inBundle("accounts.money-sell.BalanceMarketSellCommon")),
                DynamicEmoji.WARNING.getEmoji(),
                translations.get("check-the-address-network-and-quote"),
                translations.get("network"),
                translations.get("address"),
                text.escape(data.getWallet()),
                quote(data.getQuote(), translations),
                translations.get("a-transfer-to-the-wrong-address-or"),
                translations.get("the-usdt-amount-is-preliminary-the-final"),
                text.processingInfo(translations.inBundle("accounts.money-sell.BalanceMarketSellCommon")));
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, BalanceMarketSellReviewMenu option) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellReview");
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        synchronized (data) {
            if (data.isProcessing()) return UserMenu.ACCOUNTS_SELL_PROCESSING;
            if (data.getResult() != null) return UserMenu.ACCOUNTS_SELL_RESULT;
            if (option == BalanceMarketSellReviewMenu.RETURN) return leave(user);
            if (option == BalanceMarketSellReviewMenu.ADDRESS_CHANGE) {
                data.eraseSecrets();
                data.setQuote(null);
                data.setWallet(null);
                return UserMenu.ACCOUNTS_SELL_WALLET;
            }
            if (option != BalanceMarketSellReviewMenu.CONFIRM) return supportedState();
            if (data.getPassword() == null || data.getQuote() == null || BalanceMarketSellData.expired(data.getQuote())) {
                data.eraseSecrets();
                data.setQuote(null);
                sender.sendTextMes(bot, user, translations.get("the-quote-has-expired-enter-your-password"));
                return UserMenu.ACCOUNTS_SELL_PASSWORD;
            }
            return create(bot, user, data);
        }
    }

    private UserMenu create(TelegramClient bot, UserData user, BalanceMarketSellData data) {
        data.setProcessing(true);
        processingState.show(bot, user);
        BalanceMarketDto.CreateInput input = new BalanceMarketDto.CreateInput();
        input.setQuoteId(data.getQuote().getQuoteId());
        input.setAccountId(data.getAccountId());
        input.setAmount(data.getQuote().getSourceAmount());
        input.setWithdrawalMethod(data.getMethod());
        input.setWalletAddress(data.getWallet());
        input.setPaymentPassword(data.getPassword());
        input.setWalletConfirmed(true);
        try {
            var answer = endpoint.create(user.getChatId(), UUID.randomUUID().toString(), input);
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.setSession(answer.getResponse().getData());
                data.setResult(BalanceMarketSellData.Result.SESSION);
                return UserMenu.ACCOUNTS_SELL_RESULT;
            }
            if (answer.getStatus() >= 400 && answer.getStatus() < 500 && answer.getStatus() != 408) {
                var response = answer.getResponse();
                data.setFailureCode(response == null ? null : response.getCode());
                data.setFailureMessage(response == null ? null : response.getMessage());
                data.setResult(BalanceMarketSellData.Result.REJECTED);
                log.warn("Balance Market session creation rejected: HTTP {}, code {}, reason {}",
                        answer.getStatus(), response == null ? null : response.getCode(), rejectionReason(response));
                return UserMenu.ACCOUNTS_SELL_RESULT;
            }
            log.warn("Balance Market session creation outcome unknown: HTTP {}", answer.getStatus());
        } catch (RuntimeException error) {
            log.warn("Balance Market session creation outcome unknown: {}", error.getClass().getSimpleName());
        } finally {
            input.setPaymentPassword(null);
            data.eraseSecrets();
            data.setQuote(null);
            data.setProcessing(false);
        }
        data.setResult(BalanceMarketSellData.Result.UNKNOWN);
        return UserMenu.ACCOUNTS_SELL_RESULT;
    }

    private String rejectionReason(BalanceMarketDto.SessionAnswer response) {
        if (response == null) return "UNKNOWN";
        String message = response.getMessage();
        if ("MARKET_CSGO_ACCOUNT_NOT_CONFIGURED".equals(message)
                || "MANAGING_ACCOUNT_NOT_CONFIGURED".equals(message)
                || "REMAINDER_ACCOUNT_NOT_CONFIGURED".equals(message)
                || "Idempotency-Key belongs to a different request".equals(message))
            return message;
        return "UNKNOWN";
    }

    @Override
    public BalanceMarketSellReviewMenu getOption(String value) {
        return BalanceMarketSellReviewMenu.valueOf(value);
    }

    @Override
    public BalanceMarketSellReviewMenu[] getOptions(UserData user) {
        return BalanceMarketSellReviewMenu.values();
    }
    private String quote(BalanceMarketDto.Quote quote, LocalizedTexts translations) {
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>$%s</b>
                        • %s: <b>%s%%</b>
                        • %s: <b>$%s</b>
                        • %s: <b>$%s</b>
                        • %s: <b>%s USDT</b>
                        • %s: <b>%s UTC</b>
                        • %s: <b>%s UTC</b></blockquote>

                        """,
                        DynamicEmoji.GRAPH.getEmoji(),
                        translations.get("quote"),
                        translations.get("to-be-debited"),
                        text.money(quote.getSourceAmount()),
                        translations.get("total-rate"),
                        text.money(quote.getTotalFeePercent()),
                        translations.get("y-cs-fee"),
                        text.money(quote.getTotalFee()),
                        translations.get("network-fee"),
                        text.money(quote.getNetworkFee()),
                        translations.get("to-receive"),
                        text.money(quote.getEstimatedPayout()),
                        translations.get("rate-updated"),
                        text.date(quote.getRateUpdatedAt()),
                        translations.get("valid-until"),
                        text.date(quote.getExpiresAt()));
    }
}
