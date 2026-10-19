package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoTransferFailureText;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdMarketCsgoBalanceTransferDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoCollectResultState extends YTPTerminalTextMenuState {
    private final MarketCsgoCollectRegistry registry;

    public MarketCsgoCollectResultState(UserTextMessageSender sender, MarketCsgoCollectRegistry registry, BotTexts botTexts) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_COLLECT_RESULT;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-collect.MarketCsgoCollectResult");
        return format(registry.getOrCreate(user, MarketCsgoCollectData::new).getResponse(), translations);
    }

    private String format(FcdMarketCsgoBalanceTransferDto result, LocalizedTexts translations) {
        if (result == null)
            return translations.get("could-not-retrieve-the-collection-result-check");
        String amounts = result.getTransferredAmounts() == null
                ? ""
                : result.getTransferredAmounts()
                .entrySet()
                .stream()
                .map(entry -> String.format("""
                        • %s: <b>%s %s</b>
                        """,
                        translations.get("transferred-2"),
                        entry.getValue().toPlainString(),
                        entry.getKey()))
                .collect(java.util.stream.Collectors.joining());
        String balance = getBalanceStr(result, translations);
        String failure = result.isResult()
                ? ""
                : MarketCsgoTransferFailureText.format(result.getCause(), result.getFailedAccountId(), translations.inBundle("accounts.MoneyCommon"));
        boolean unknown = result.getAccounts() != null && result
                .getAccounts()
                .stream()
                .anyMatch(account -> "UNKNOWN".equals(account.getStatus()));
        String notice = unknown
                ? String.format("<i>%s</i>",
                        translations.get("some-transfers-have-an-unknown-outcome-do"))
                : "";
        String retry = result.isResult()
                ? ""
                : String.format("<i>%s</i>",
                        translations.get("check-market-csgo-history-before-trying-again"));
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b>
                        %s%s</blockquote>
                        %s%s%s%s
                        """,
                        result.isResult() ? DynamicEmoji.SUCCESS.getEmoji() : DynamicEmoji.WARNING.getEmoji(),
                        result.isResult() ? translations.get("collection-completed") : translations.get("collection-stopped"),
                        translations.get("confirmed-transfers"),
                        result.getTransferredTokens(),
                        amounts,
                        balance.stripTrailing(),
                        failure,
                        notice,
                        accounts(result, translations),
                        retry);
    }

    @NotNull
    private static String getBalanceStr(FcdMarketCsgoBalanceTransferDto result, LocalizedTexts translations) {
        String balanceSource = result.getBalanceSource() == null
                ? ""
                : result.getBalanceSource();
        String source = switch (balanceSource) {
            case "CALCULATED" -> String.format(" <i>(%s</i>",
                    translations.get("calculated"));
            case "DATABASE_FALLBACK" -> String.format(" <i>(%s</i>",
                    translations.get("calculated-using-the-stored-balance"));
            default -> "";
        };
        return result.getDestinationBalanceAfter() == null
                ? ""
                : String.format("""
                        • %s: <b>%s %s</b>%s
                        """,
                        translations.get("recipient-balance"),
                        result.getDestinationBalanceAfter().toPlainString(),
                        result.getCurrency() == null ? "" : result.getCurrency(),
                        source);
    }

    private String accounts(FcdMarketCsgoBalanceTransferDto result, LocalizedTexts translations) {
        if (result.getAccounts() == null || result.getAccounts().isEmpty()) return "";
        String rows = result
                .getAccounts()
                .stream()
                .limit(30)
                .map(value -> this.getAccountTransferStr(value, translations))
                .collect(java.util.stream.Collectors.joining("\n"));
        String more = result.getAccounts().size() > 30 ? String.format("""

                • %s: %s
                """,
                translations.get("more-accounts"),
                result.getAccounts().size() - 30).stripTrailing() : "";
        return String.format("""
                <blockquote expandable><b>%s</b>
                %s%s</blockquote>
                """,
                translations.get("by-account"),
                rows,
                more);
    }

    private String getAccountTransferStr(FcdMarketCsgoBalanceTransferDto.AccountTransfer account, LocalizedTexts translations) {
        String status = switch (account.getStatus() == null ? "" : account.getStatus()) {
            case "TRANSFERRED" -> translations.get("transferred");
            case "SKIPPED_ZERO_BALANCE" -> translations.get("zero-balance");
            case "NOT_ATTEMPTED" -> translations.get("not-processed");
            default -> translations.get("outcome-unknown");
        };
        String balance = account.getBalanceAfter() == null
                ? ""
                : String.format(", %s",
                        translations.format("remaining", Map.of("value1", String.format("<b>%s %s</b>", account.getBalanceAfter().toPlainString(), account.getCurrency() == null ? "" : account.getCurrency())))).stripTrailing();
        return String.format("• <code>%s</code> — %s%s",
                account.getTokenId(), status, balance).stripTrailing();
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }
}
