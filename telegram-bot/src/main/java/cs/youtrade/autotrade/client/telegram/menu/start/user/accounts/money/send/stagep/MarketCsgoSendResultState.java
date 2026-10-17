package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stagep;

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
                                     MarketCsgoSendTransfer transfer) {
        super(sender, registry);
        this.transfer = transfer;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_RESULT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        if (data.getResult() == null) return summary(user);
        String result = switch (data.getResult()) {
            case TEST_UNKNOWN -> """
                    Результат пробного перевода неизвестен. Остаток не отправлен.
                    Проверьте историю Market.CSGO перед новой попыткой.
                    """;
            case TEST_FAILED -> """
                    Пробный перевод не удался. Остаток не отправлен.
                    Проверьте баланс, валюту и данные получателя.
                    """;
            case NOT_RECEIVED -> """
                    Пробный перевод отправлен. Остаток не отправлен.
                    Проверьте историю Market.CSGO и аккаунт получателя.
                    """;
            case REMAINDER_UNKNOWN -> """
                    Пробный перевод подтверждён, результат отправки остатка неизвестен.
                    Не повторяйте перевод до проверки истории Market.CSGO.
                    """;
            case REMAINDER_FAILED -> """
                    Пробный перевод подтверждён, но остаток не отправлен.
                    Проверьте доступный баланс и историю Market.CSGO.
                    """;
            case COMPLETED -> String.format("""
                    %s <b>Перевод завершён</b>
                    <blockquote>Отправлено: <b>$%s</b>, включая пробные $%s.</blockquote>
                    """, DynamicEmoji.SUCCESS.getEmoji(), money(data.getAmount()), transfer.testAmount().toPlainString());
        };
        String details = data.getResponse() == null || data.getResponse().isResult() ? ""
                : MarketCsgoTransferFailureText.format(data.getResponse().getCause(), data.getResponse().getFailedAccountId());
        return String.format("""
                %s%s%s
                """, summary(user), result, details);
    }
}
