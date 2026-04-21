package cs.youtrade.autotrade.client.telegram.menu.start.topup.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayData;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserPayAmountState extends YTPTextState {
    private final UserPayRegistry registry;

    public UserPayAmountState(
            UserTextMessageSender sender,
            UserPayRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return String.format("""            
                        %s <b>Введите сумму пополнения в $ (USD)</b>
                        └ <b>Пример</b>: <i>$</i><code>25</code> • <i>$</i><code>100</code> • <i>$</i><code>250</code> • <i>$</i><code>1000</code>
                        """,
                DynamicEmoji.MONEY.getEmoji()
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOP_UP_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.START;
        }

        String input = update.getMessage().getText();
        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount <= 0) {
                sender.sendTextMes(bot, user,
                        "#2: Введенное значение не является положительное числом");
                return UserMenu.START;
            }
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format(
                    "#1: Введенное значение не является положительное числом: %s", input));
            return UserMenu.START;
        }

        var data = registry.getOrCreate(user, UserPayData::new);
        data.setAmount(amount);
        return UserMenu.TOP_UP_STAGE_P;
    }
}
