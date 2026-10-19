package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.UserFollowData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.UserFollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Objects;

@Service
public class FollowPcoState extends YTPTextState {
    private final UserFollowRegistry registry;

    public FollowPcoState(
            UserTextMessageSender sender,
            UserFollowRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.follow.FollowRequest");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>%s</blockquote>
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("enter-a-mode-number-or-name-from"),
                        ParamsCopyOptions.generateDescription(translations.inBundle("parameters.follow.ParamsCopyOptions")));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.follow.FollowRequest");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.FOLLOW;
        }

        String input = update.getMessage().getText();
        ParamsCopyOptions pco;
        try {
            // Если пользователь прислал число
            short pcoOrdinal = Short.parseShort(input);
            pco = ParamsCopyOptions.getOrdinal(pcoOrdinal);
        } catch (NumberFormatException e) {
            pco = ParamsCopyOptions.getOrdinal(input);
            if (Objects.isNull(pco)) {
                sender.sendTextMes(bot, user, String.format("#1: %s",
                        translations.get("copy-settings-not-found-going-back")));
                return UserMenu.FOLLOW;
            }
        }
        var data = registry.getOrCreate(user, UserFollowData::new);
        data.setPco(pco);
        return UserMenu.FOLLOW_STAGE_P;
    }
}
