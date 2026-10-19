package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.UserAutoSellUpdateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.UserAutoSellUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoSellUpdateValueState extends YTPTextState {
    private final UserAutoSellUpdateRegistry registry;

    public AutoSellUpdateValueState(
            UserTextMessageSender sender,
            UserAutoSellUpdateRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.autosell.AutoSellUpdate");
        var data = registry.getOrCreate(userData, UserAutoSellUpdateData::new);
        return String.format("""
                        %s <b>%s</b>
                        └ %s
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("enter-a-value-for-the-selected-field"),
                        data.getField().getForkByField(translations.inBundle("parameters.TdpField")));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autosell.AutoSellUpdate");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.AUTOSELL;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoSellUpdateData::new);
        data.setValue(value);
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_P;
    }
}
