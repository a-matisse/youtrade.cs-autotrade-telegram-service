package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.UserAutoSellUpdateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.UserAutoSellUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.TdpField;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoSellUpdateFieldState extends YTPTextState {
    private final UserAutoSellUpdateRegistry registry;

    public AutoSellUpdateFieldState(
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
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote>%s</blockquote>
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("enter-the-field-you-want-to-edit"),
                        TdpField.generateDescription(TdpField.DirType.SELL, translations.inBundle("parameters.TdpField")));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autosell.AutoSellUpdate");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.AUTOSELL;
        }

        String field = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoSellUpdateData::new);
        TdpField tdpF = TdpField.fromFName(field);
        if (tdpF == null) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("field-not-found-going-back")));
            return UserMenu.SCORING;
        }

        data.setField(tdpF);
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_2;
    }
}
