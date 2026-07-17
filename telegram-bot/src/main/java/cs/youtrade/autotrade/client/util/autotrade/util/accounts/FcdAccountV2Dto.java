package cs.youtrade.autotrade.client.util.autotrade.util.accounts;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.UserAccountsMetaData;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public String asMessage(UserAccountsMetaData data) {
        return switch (data.getMode()) {
            case GENERAL -> asMessage(data, this::asGeneral);
            case BUYER -> asMessage(data, this::asBuyer);
            case SELLER -> asMessage(data, this::asSeller);
            case WORKER -> asMessage(data, this::asWorker);
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

    private String asGeneral(UserAccountsMetaData data) {
        return String.format("┗ <b>Покупка %s — Продажа %s — Воркер %s</b>",
                decideEmoji(buyBalance != null), decideEmoji(sellBalance != null), decideEmoji(workerStatus != null));
    }

    private String asBuyer(UserAccountsMetaData data) {
        // Если токен не подключен
        if (buyBalance == null)
            return String.format("┗ <b>%s Покупка не подключена</b>", decideEmoji(false));
        // Если токен подключен
        return String.format("┗ <b>%s %s</b> — %s",
                DynamicEmoji.ITEM_RECEIVE.getEmoji(), getMarketWithLink(data.getYdp().getSource()), balanceStr(buyBalance));
    }

    private String asSeller(UserAccountsMetaData data) {
        // Если токен не подключен
        if (sellBalance == null)
            return String.format("┗ <b>%s Продажа не подключена</b>", decideEmoji(false));
        // Если токен подключен
        return String.format("┗ <b>%s %s</b> — %s",
                DynamicEmoji.ITEM_SEND.getEmoji(), getMarketWithLink(data.getYdp().getDestination()), balanceStr(sellBalance));
    }

    private String asWorker(UserAccountsMetaData data) {
        // Если токен не подключен
        if (workerStatus == null)
            return String.format("┗ <b>%s Воркер не подключен</b>", decideEmoji(false));
        // Если токен подключен
        return String.format("┗ <b>%s Код</b>: <tg-spoiler><i>%s</i></tg-spoiler> <b>[Приём %s — Передача %s]</b>",
                DynamicEmoji.WORKER.getEmoji(),
                workerStatus.getGuardCode(),
                workerStatus.getReceiveStatus().getEmoji().getEmoji(),
                workerStatus.getWithdrawStatus().getEmoji().getEmoji()
        );
    }

    // --- Вспомогательные методы
    private String nameStr() {
        return givenName == null
                ? String.format("<code>%s</code>", steamToken)
                : String.format("<code>%s</code> [<b>%s</b>]", steamToken, givenName);
    }

    private String balanceStr(AccountData balance) {
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
        return String.format("<b>$%s</b> (<i>Холд <b>$%s</b></i>)", available, frozen);
    }

    private String decideEmoji(boolean b) {
        return b ? DynamicEmoji.ON.getEmoji() : DynamicEmoji.OFF.getEmoji();
    }
}
