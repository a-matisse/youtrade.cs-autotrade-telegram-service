package cs.youtrade.autotrade.client.telegram.menu.main.params.rename.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.rename.UserRenameData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.rename.UserParamsRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ParamsRenameValueState extends AbstractTextState {
    private final UserParamsRenameRegistry registry;

    public ParamsRenameValueState(
            UserTextMessageSender sender,
            UserParamsRenameRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return "Введите новое имя для набора параметров...";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_RENAME_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.PARAMS;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setValue(value);
        return UserMenu.PARAMS_RENAME_STAGE_P;
    }
}
