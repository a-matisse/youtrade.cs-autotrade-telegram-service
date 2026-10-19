package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.unfollow.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.unfollow.UserUnfollowData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.unfollow.UserUnfollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsFollowDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class UnfollowIdState extends YTPTextState {
    private final UserUnfollowRegistry registry;
    private final ParamsEndpoint endpoint;

    public UnfollowIdState(
            UserTextMessageSender sender,
            UserUnfollowRegistry registry,
            ParamsEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.follow.FollowRequest");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>%s</blockquote>
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("enter-the-follow-id-to-unfollow"),
                        getFollowStr(userData));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_UNFOLLOW_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.follow.FollowRequest");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.WORDS;
        }

        String input = update.getMessage().getText();
        long followId;
        try {
            followId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                    translations.get("the-value-entered-is-not-a-number"),
                    input));
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, UserUnfollowData::new);
        data.setFollowId(followId);
        return UserMenu.FOLLOW_UNFOLLOW_STAGE_P;
    }

    private String getFollowStr(UserData user) {
        var translations = botTexts.forUser(user, "parameters.follow.FollowRequest");
        var restAns = endpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = fcd.getData().getFollows();
        if (data.isEmpty())
            return translations.get("the-follow-id-list-is-empty");

        return data
                .stream()
                .map(value -> value.asMessage(translations.inBundle("summary.ParamsSummary")))
                .collect(Collectors.joining("\n"));
    }
}
