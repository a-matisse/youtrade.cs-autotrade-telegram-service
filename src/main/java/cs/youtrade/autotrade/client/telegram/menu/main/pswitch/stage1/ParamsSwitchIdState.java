package cs.youtrade.autotrade.client.telegram.menu.main.pswitch.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.pswitch.ParamsSwitchData;
import cs.youtrade.autotrade.client.telegram.menu.main.pswitch.ParamsSwitchRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ParamsSwitchIdState extends AbstractTextState {
    private final ParamsSwitchRegistry registry;

    public ParamsSwitchIdState(
            UserTextMessageSender sender,
            ParamsSwitchRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage() {
        return """
                📋 Переключение на другие параметры
                
                Введите ID параметров для загрузки:
                
                Пример: 12345
                """;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_SWITCH_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение в меню (/menu).");
            return UserMenu.MAIN;
        }

        String input = update.getMessage().getText();
        var data = registry.getOrCreate(user, ParamsSwitchData::new);
        data.setInput(input);
        return UserMenu.MAIN_PARAMETERS_SWITCH_STAGE_2;
    }
}
