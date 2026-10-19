package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.stage3;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.ScoringEditData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.ScoringEditRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ScoringEditValueState extends YTPTextState {
    private final ScoringEditRegistry registry;

    public ScoringEditValueState(
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
        var data = registry.getOrCreate(userData, ScoringEditData::new);
        return String.format("""
                        %s <b>%s</b>
                        └ %s
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("enter-a-value-for-the-selected-field"),
                        data.getField().getForkByField(translations.inBundle("parameters.scoring.YdpField")));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_EDIT_STAGE_3;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.scoring.UserScoring");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.SCORING;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, ScoringEditData::new);
        data.setValue(value);
        return UserMenu.SCORING_EDIT_STAGE_P;
    }
}
