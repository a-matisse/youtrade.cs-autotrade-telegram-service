package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stagep;

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

    public MarketCsgoCollectResultState(UserTextMessageSender sender, MarketCsgoCollectRegistry registry) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_COLLECT_RESULT;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return format(registry.getOrCreate(user, MarketCsgoCollectData::new).getResponse());
    }

    private String format(FcdMarketCsgoBalanceTransferDto result) {
        if (result == null)
            return "Не удалось получить результат сбора. Перед новым запросом проверьте балансы и историю Market.CSGO.";
        String amounts = result.getTransferredAmounts() == null
                ? ""
                : result.getTransferredAmounts()
                .entrySet()
                .stream()
                .map(entry -> String.format("""
                        • Переведено: <b>%s %s</b>
                        """, entry.getValue().toPlainString(), entry.getKey()))
                .collect(java.util.stream.Collectors.joining());
        String balance = getBalanceStr(result);
        String failure = result.isResult()
                ? ""
                : MarketCsgoTransferFailureText.format(result.getCause(), result.getFailedAccountId());
        boolean unknown = result.getAccounts() != null && result
                .getAccounts()
                .stream()
                .anyMatch(account -> "UNKNOWN".equals(account.getStatus()));
        String notice = unknown
                ? "<i>Есть переводы с неизвестным результатом. Не повторяйте сбор до проверки истории Market.CSGO.</i>"
                : "";
        String retry = result.isResult()
                ? ""
                : "<i>Проверьте историю Market.CSGO перед повтором.</i>";
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• Подтверждено переводов: <b>%s</b>
                        %s%s</blockquote>
                        %s%s%s%s
                        """,
                result.isResult() ? DynamicEmoji.SUCCESS.getEmoji() : DynamicEmoji.WARNING.getEmoji(),
                result.isResult() ? "Сбор завершён" : "Сбор остановлен", result.getTransferredTokens(),
                amounts, balance.stripTrailing(), failure, notice, accounts(result), retry
        );
    }

    @NotNull
    private static String getBalanceStr(FcdMarketCsgoBalanceTransferDto result) {
        String balanceSource = result.getBalanceSource() == null
                ? ""
                : result.getBalanceSource();
        String source = switch (balanceSource) {
            case "CALCULATED" -> " <i>(по расчёту)</i>";
            case "DATABASE_FALLBACK" -> " <i>(расчёт с балансом из БД)</i>";
            default -> "";
        };
        return result.getDestinationBalanceAfter() == null
                ? ""
                : String.format("""
                        • Баланс получателя: <b>%s %s</b>%s
                        """,
                result.getDestinationBalanceAfter().toPlainString(),
                result.getCurrency() == null ? "" : result.getCurrency(), source);
    }

    private String accounts(FcdMarketCsgoBalanceTransferDto result) {
        if (result.getAccounts() == null || result.getAccounts().isEmpty()) return "";
        String rows = result
                .getAccounts()
                .stream()
                .limit(30)
                .map(this::getAccountTransferStr)
                .collect(java.util.stream.Collectors.joining("\n"));
        String more = result.getAccounts().size() > 30 ? String.format("""

                • Ещё аккаунтов: %s
                """, result.getAccounts().size() - 30).stripTrailing() : "";
        return String.format("""
                <blockquote expandable><b>По аккаунтам</b>
                %s%s</blockquote>
                """, rows, more);
    }

    private String getAccountTransferStr(FcdMarketCsgoBalanceTransferDto.AccountTransfer account) {
        String status = switch (account.getStatus() == null ? "" : account.getStatus()) {
            case "TRANSFERRED" -> "переведено";
            case "SKIPPED_ZERO_BALANCE" -> "нулевой баланс";
            case "NOT_ATTEMPTED" -> "не обработан";
            default -> "результат неизвестен";
        };
        String balance = account.getBalanceAfter() == null
                ? ""
                : String.format(", остаток <b>%s %s</b>",
                account.getBalanceAfter().toPlainString(),
                account.getCurrency() == null ? "" : account.getCurrency()).stripTrailing();
        return String.format("• <code>%s</code> — %s%s",
                account.getTokenId(), status, balance).stripTrailing();
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }
}
