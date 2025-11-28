package cs.youtrade.autotrade.client.telegram.menu.start.pay.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.pay.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserPayAmountState extends AbstractTextState {
    private final UserPayRegistry registry;

    public UserPayAmountState(
            UserTextMessageSender sender,
            UserPayRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage() {
        return "Пожалуйста, введите сумму в USD для пополнения...";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOP_UP_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.SCORING;
        }

        String input = update.getMessage().getText();
        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount < 4d) {
                sender.sendTextMes(bot, chatId, "#2: Получено пустое сообщение. Возвращение обратно...");
                return UserMenu.SCORING;
            }
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.SCORING;
        }

        var data = registry.get(user);
        data.setAmount(amount);
        return UserMenu.TOP_UP_STAGE_P;
    }
}
