package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.UserFollowData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.UserFollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class FollowIdState extends YTPTextState {
    private final UserFollowRegistry registry;

    public FollowIdState(
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
        return String.format("%s %s",
                DynamicEmoji.WRITE.getEmoji(),
                translations.format("enter-a-params-id-b", Map.of("emphasis", String.format("<b>%s</b>", translations.get("enter-a-params-id-b.emphasis")))));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_STAGE_1;
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
        long paramsId;
        try {
            paramsId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                    translations.get("the-value-entered-is-not-a-number"),
                    input));
            return UserMenu.FOLLOW;
        }

        var data = registry.getOrCreate(user, UserFollowData::new);
        data.setYourTdpId(paramsId);
        return UserMenu.FOLLOW_STAGE_2;
    }
}
