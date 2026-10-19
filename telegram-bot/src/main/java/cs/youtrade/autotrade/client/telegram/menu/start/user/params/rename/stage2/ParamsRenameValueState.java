package cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
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
            UserParamsRenameRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsRename");
        return String.format("%s",
                translations.format("now-enter-a-name-b-for-the", Map.of("emphasis", String.format("<b>%s</b>", translations.get("now-enter-a-name-b-for-the.emphasis")))));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_RENAME_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.ParamsRename");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.USER;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setValue(value);
        return UserMenu.PARAMS_RENAME_STAGE_P;
    }
}
