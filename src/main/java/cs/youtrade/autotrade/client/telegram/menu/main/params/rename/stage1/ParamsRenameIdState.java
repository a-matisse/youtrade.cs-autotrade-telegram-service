package cs.youtrade.autotrade.client.telegram.menu.main.params.rename.stage1;

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
public class ParamsRenameIdState extends AbstractTextState {
    private final UserParamsRenameRegistry registry;

    public ParamsRenameIdState(
            UserTextMessageSender sender, 
            UserParamsRenameRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage() {
        return "Пожалуйста, введите params-ID для смены имени:";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_RENAME_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.PARAMS;
        }

        String input = update.getMessage().getText();
        long paramsId;
        try {
            paramsId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.PARAMS;
        }

        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setId(paramsId);
        return UserMenu.PARAMS_RENAME_STAGE_2;
    }
}
