package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage4;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendTransfer;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MarketCsgoSendAmountState extends AbstractMarketCsgoSendState<MarketCsgoSendAmountMenu> {
    private final MarketCsgoSendTransfer transfer;

    public MarketCsgoSendAmountState(UserTextMessageSender sender, MarketCsgoSendRegistry registry,
                                     MarketCsgoSendTransfer transfer) {
        super(sender, registry);
        this.transfer = transfer;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_AMOUNT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Выберите сумму или отправьте её сообщением</b>
                └ <i>«Всё» — доступный баланс с учётом пробного перевода.</i>
                """, summary(user), DynamicEmoji.WRITE.getEmoji());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        try {
            return choose(bot, user, new BigDecimal(update.getMessage().getText().trim().replace(',', '.')));
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Не удалось распознать сумму. Пример: <code>200.00</code>");
            return supportedState();
        }
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, MarketCsgoSendAmountMenu option) {
        if (option == MarketCsgoSendAmountMenu.RETURN) return leave(user);
        return switch (option) {
            case AMOUNT_200 -> choose(bot, user, new BigDecimal("200"));
            case AMOUNT_500 -> choose(bot, user, new BigDecimal("500"));
            case AMOUNT_1000 -> choose(bot, user, new BigDecimal("1000"));
            case ALL -> choose(bot, user, registry.getOrCreate(user, MarketCsgoSendData::new).getAvailable().setScale(3, RoundingMode.DOWN));
            default -> supportedState();
        };
    }

    private UserMenu choose(TelegramClient bot, UserData user, BigDecimal amount) {
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        if (amount.signum() <= 0 || amount.scale() > 3) {
            sender.sendTextMes(bot, user, "Сумма должна быть положительной и содержать не более трёх знаков после запятой.");
        } else if (amount.compareTo(transfer.testAmount()) < 0) {
            sender.sendTextMes(bot, user, "Минимальная сумма с проверкой получателя — $"
                    + transfer.testAmount().toPlainString() + ".");
        } else if (amount.compareTo(data.getAvailable()) > 0) {
            sender.sendTextMes(bot, user, "Сумма больше доступного баланса: $" + money(data.getAvailable()) + ".");
        } else {
            data.setAmount(amount);
            return UserMenu.ACCOUNTS_SEND_CONFIRM;
        }
        return supportedState();
    }

    @Override
    public MarketCsgoSendAmountMenu getOption(String value) {
        return MarketCsgoSendAmountMenu.valueOf(value);
    }

    @Override
    public MarketCsgoSendAmountMenu[] getOptions(UserData user) {
        return MarketCsgoSendAmountMenu.values();
    }
}
