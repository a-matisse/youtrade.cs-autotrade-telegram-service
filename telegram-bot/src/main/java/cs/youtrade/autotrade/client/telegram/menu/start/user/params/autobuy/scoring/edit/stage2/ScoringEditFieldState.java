package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.ScoringEditData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.ScoringEditRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.YdpField;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ScoringEditFieldState extends YTPTextState {
    private final ScoringEditRegistry registry;

    public ScoringEditFieldState(
            UserTextMessageSender sender,
            ScoringEditRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.scoring.UserScoring");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote>%s</blockquote>
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("enter-the-field-you-want-to-edit"),
                        YdpField.generateDescription(translations.inBundle("parameters.scoring.YdpField")));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_EDIT_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.scoring.UserScoring");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.SCORING;
        }

        String fName = update.getMessage().getText();
        var data = registry.getOrCreate(user, ScoringEditData::new);
        YdpField field = YdpField.fromFName(fName);
        if (field == null) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("field-not-found-going-back")));
            return UserMenu.SCORING;
        }

        data.setField(field);
        return UserMenu.SCORING_EDIT_STAGE_3;
    }
}
