package cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.stage3;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.QuickConfigCreateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.QuickConfigCreateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;

@Service
public class ConfigCreateBankSizeState extends AbstractTextState {
    private final QuickConfigCreateRegistry registry;

    public ConfigCreateBankSizeState(
            UserTextMessageSender sender,
            QuickConfigCreateRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_QUICK_CONFIG_INIT_STAGE_3;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.START;
        }

        String input = update.getMessage().getText();
        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount <= 0) {
                sender.sendTextMes(bot, chatId,
                        "#2: Введенное значение не является положительное числом");
                return UserMenu.START;
            }
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format(
                    "#1: Введенное значение не является положительное числом: %s", input));
            return UserMenu.START;
        }

        var data = registry.getOrCreate(user, QuickConfigCreateData::new);
        data.setPreferredTradeCapital(BigDecimal.valueOf(amount));
        return UserMenu.PARAMS_QUICK_CONFIG_INIT_STAGE_P;
    }

    @Override
    protected String getMessage(UserData user) {
        return """
                💰 <b>Размер банка для торговли</b>
                ━━━━━━━━━━━━━━━━━━━━━
                
                Укажите примерный размер банка в USD — сумму, выделяемую под трейд.
                Это не обязательный расход, а ориентир масштаба операций.
                
                Пример: 5000
                """;
    }
}
