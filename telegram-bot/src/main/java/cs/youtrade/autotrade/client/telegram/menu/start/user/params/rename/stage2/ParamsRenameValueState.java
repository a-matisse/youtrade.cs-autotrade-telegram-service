package cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserRenameData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserParamsRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ParamsRenameValueState extends YTPTextState {
    private final UserParamsRenameRegistry registry;

    public ParamsRenameValueState(
            UserTextMessageSender sender,
            UserParamsRenameRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return "<b>Теперь введите имя</b> для выбранных параметров...";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_RENAME_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.USER;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setValue(value);
        return UserMenu.PARAMS_RENAME_STAGE_P;
    }
}
