package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.update.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.update.UserAutoSellUpdateData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.update.UserAutoSellUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoSellUpdateValueState extends AbstractTextState {
    private final UserAutoSellUpdateRegistry registry;

    public AutoSellUpdateValueState(
            UserTextMessageSender sender,
            UserAutoSellUpdateRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return """
                Теперь укажите значение для выбранного поля...
                
                Для настройки автопродажи используйте следующие шаблоны:
                - minAutoSellProfit — Допустимая рентабельность: от 0 до maxAutoSellProfit
                - maxAutoSellProfit — Допустимая рентабельность: не меньше minAutoSellProfit
                - evalmodec1 — Допустимое значение коэффициента: от 1 до 99 (включительно)
                """;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.AUTOSELL;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoSellUpdateData::new);
        data.setValue(value);
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_P;
    }
}
