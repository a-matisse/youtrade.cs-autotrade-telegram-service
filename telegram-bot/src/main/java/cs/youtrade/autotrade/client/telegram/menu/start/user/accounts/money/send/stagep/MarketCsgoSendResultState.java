package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendTerminalState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendTransfer;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoTransferFailureText;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoSendResultState extends AbstractMarketCsgoSendTerminalState {
    private final MarketCsgoSendTransfer transfer;

    public MarketCsgoSendResultState(UserTextMessageSender sender, MarketCsgoSendRegistry registry,
                                     MarketCsgoSendTransfer transfer, BotTexts botTexts) {
        super(sender, registry, botTexts);
        this.transfer = transfer;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_RESULT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSendResult");
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        if (data.getResult() == null) return summary(user);
        String result = switch (data.getResult()) {
            case TEST_UNKNOWN -> String.format("""
                    %s
                    %s
                    """,
                    translations.get("the-test-transfer-outcome-is-unknown-the"),
                    translations.get("check-market-csgo-history-before-trying-again"));
            case TEST_FAILED -> String.format("""
                    %s
                    %s
                    """,
                    translations.get("the-test-transfer-failed-the-remaining-amount"),
                    translations.get("check-the-balance-currency-and-recipient-details"));
            case NOT_RECEIVED -> String.format("""
                    %s
                    %s
                    """,
                    translations.get("the-test-transfer-was-sent-the-remaining"),
                    translations.get("check-market-csgo-history-and-the-recipient"));
            case REMAINDER_UNKNOWN -> String.format("""
                    %s
                    %s
                    """,
                    translations.get("the-test-transfer-is-confirmed-but-the-2"),
                    translations.get("do-not-repeat-the-transfer-until-you"));
            case REMAINDER_FAILED -> String.format("""
                    %s
                    %s
                    """,
                    translations.get("the-test-transfer-is-confirmed-but-the"),
                    translations.get("check-the-available-balance-and-market-csgo"));
            case COMPLETED -> String.format("""
                    %s <b>%s</b>
                    <blockquote>%s</blockquote>
                    """,
                    DynamicEmoji.SUCCESS.getEmoji(),
                    translations.get("transfer-completed"),
                    translations.format("sent-including-the-test-transfer-of", Map.of("value1", String.format("<b>$%s</b>", money(data.getAmount())), "argument3", String.valueOf(transfer.testAmount().toPlainString()))));
        };
        String details = data.getResponse() == null || data.getResponse().isResult() ? ""
                : MarketCsgoTransferFailureText.format(data.getResponse().getCause(), data.getResponse().getFailedAccountId(), translations.inBundle("accounts.MoneyCommon"));
        return String.format("""
                %s%s%s
                """, summary(user), result, details);
    }
}
