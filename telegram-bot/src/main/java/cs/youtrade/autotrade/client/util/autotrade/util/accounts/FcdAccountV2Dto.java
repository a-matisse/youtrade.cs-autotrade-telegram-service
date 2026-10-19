package cs.youtrade.autotrade.client.util.autotrade.util.accounts;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.UserAccountsMetaData;
import cs.youtrade.autotrade.client.util.autotrade.util.HistoryDateTimeFormat;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.function.Function;

import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.getMarketWithLink;

@Value
@Builder
public class FcdAccountV2Dto {
    Long id;
    String steamToken;
    String givenName;
    AccountData buyBalance;
    AccountData sellBalance;
    WorkerData workerStatus;

    public String asMessage(UserAccountsMetaData data, LocalizedTexts translations) {
        return switch (data.getMode()) {
            case GENERAL -> asMessage(data, value -> this.asGeneral(value, translations));
            case BUYER -> asMessage(data, value -> this.asBuyer(value, translations));
            case SELLER -> asMessage(data, value -> this.asSeller(value, translations));
            case WORKER -> asMessage(data, value -> this.asWorker(value, translations));
        };
    }

    // --- Section for different view types
    private String asMessage(UserAccountsMetaData data, Function<UserAccountsMetaData, String> dataFunction) {
        return String.format("""
                        <code>%d</code> — %s
                        %s""",
                id, nameStr(),
                dataFunction.apply(data));
    }

    private String asGeneral(UserAccountsMetaData data, LocalizedTexts translations) {
        return String.format("┗ <b>%s</b>",
                translations.format("buying-selling-y-cs-worker", Map.of("argument1", String.valueOf(decideEmoji(buyBalance != null)), "argument2", String.valueOf(decideEmoji(sellBalance != null)), "argument3", String.valueOf(decideEmoji(workerStatus != null)))));
    }

    private String asBuyer(UserAccountsMetaData data, LocalizedTexts translations) {
        // Если токен не подключен
        if (buyBalance == null)
            return String.format("┗ <b>%s %s</b>",
                    decideEmoji(false),
                    translations.get("buying-is-not-connected"));
        // Если токен подключен
        return String.format("┗ <b>%s %s</b> · %s — %s",
                DynamicEmoji.ITEM_RECEIVE.getEmoji(), getMarketWithLink(data.getYdp().getSource()),
                purchaseFreezeStr(translations), balanceStr(buyBalance, translations));
    }

    private String asSeller(UserAccountsMetaData data, LocalizedTexts translations) {
        // Если токен не подключен
        if (sellBalance == null)
            return String.format("┗ <b>%s %s</b>",
                    decideEmoji(false),
                    translations.get("selling-is-not-connected"));
        // Если токен подключен
        return String.format("┗ <b>%s %s</b> — %s",
                DynamicEmoji.ITEM_SEND.getEmoji(), getMarketWithLink(data.getYdp().getDestination()), balanceStr(sellBalance, translations));
    }

    private String asWorker(UserAccountsMetaData data, LocalizedTexts translations) {
        // Если токен не подключен
        if (workerStatus == null)
            return String.format("┗ <b>%s %s</b>",
                    decideEmoji(false),
                    translations.get("y-cs-worker-is-not-connected"));
        // Если токен подключен
        return String.format("┗ <b>%s %s</b>: <tg-spoiler><i>%s</i></tg-spoiler> <b>[%s %s — %s %s]</b>",
                DynamicEmoji.WORKER.getEmoji(), translations.get("code"), workerStatus.getGuardCode(),
                translations.get("receive"), workerStatus.getReceiveStatus().getEmoji().getEmoji(),
                translations.get("send"), workerStatus.getWithdrawStatus().getEmoji().getEmoji());
    }

    // --- Вспомогательные методы
    private String nameStr() {
        return givenName == null
                ? String.format("<code>%s</code>", steamToken)
                : String.format("<code>%s</code> [<b>%s</b>]", steamToken, givenName);
    }

    private String balanceStr(AccountData balance, LocalizedTexts translations) {
        String available = BigDecimal
                .valueOf(balance.getAvailable())
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
        // Возвращаем без замороженного баланса, если пусто
        if (balance.getFrozen() == null || balance.getFrozen() <= 0)
            return String.format("<b>$%s</b>", available);
        // Возвращаем полную строку
        String frozen = BigDecimal
                .valueOf(balance.getFrozen())
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
        return String.format("<b>$%s</b> (<i>%s <b>$%s</b></i>)", available, translations.get("hold"), frozen);
    }

    private String purchaseFreezeStr(LocalizedTexts translations) {
        boolean userFrozen = Boolean.TRUE.equals(buyBalance.getUserPurchaseFrozen());
        boolean systemFrozen = Boolean.TRUE.equals(buyBalance.getSystemPurchaseFrozen())
                || Boolean.TRUE.equals(buyBalance.getAccountFrozen());
        if (!userFrozen && !systemFrozen) return DynamicEmoji.SUN.getEmoji();

        LocalDateTime until = null;
        try {
            if (userFrozen && buyBalance.getUserPurchaseUnfreezeAt() != null)
                until = HistoryDateTimeFormat.parse(buyBalance.getUserPurchaseUnfreezeAt());
            if (systemFrozen && buyBalance.getAccountUnfreezeAt() != null) {
                LocalDateTime systemUntil = HistoryDateTimeFormat.parse(buyBalance.getAccountUnfreezeAt());
                if (until == null || systemUntil.isAfter(until)) until = systemUntil;
            }
        } catch (DateTimeParseException error) {
            return DynamicEmoji.MARKET_MARKET.getEmoji();
        }
        if (until == null) return DynamicEmoji.MARKET_MARKET.getEmoji();

        Duration remaining = Duration.between(LocalDateTime.now(), until);
        if (remaining.isNegative() || remaining.isZero()) return DynamicEmoji.SUN.getEmoji();
        long days = remaining.toDays();
        if (!remaining.minusDays(days).isZero()) days++;
        return DynamicEmoji.MARKET_MARKET.getEmoji() + " " + days + translations.get("d");
    }

    private String decideEmoji(boolean b) {
        return b ? DynamicEmoji.ON.getEmoji() : DynamicEmoji.OFF.getEmoji();
    }
}
