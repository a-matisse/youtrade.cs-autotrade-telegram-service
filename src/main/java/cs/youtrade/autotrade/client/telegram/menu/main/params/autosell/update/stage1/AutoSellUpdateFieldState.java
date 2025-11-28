package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.update.stage1;

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
public class AutoSellUpdateFieldState extends AbstractTextState {
    private final UserAutoSellUpdateRegistry registry;

    public AutoSellUpdateFieldState(
            UserTextMessageSender sender,
            UserAutoSellUpdateRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return """
                Введите название поля для изменения...
                
                Для настройки автопродажи используйте следующие шаблоны:
                - minAutoSellProfit — Минимальный процент рентабельности позиции
                - maxAutoSellProfit — Максимальный процент рентабельности позиции
                - evalmodec1 — Первый коэффициент EvalMode для настройки автопродажи
                """;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.AUTOSELL;
        }

        String field = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoSellUpdateData::new);
        data.setField(field);
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_2;
    }
}
