package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.UserFollowOperationType;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.UserFollowData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.UserFollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class FollowChooseState extends YTPTextMenuState<UserFollowOperationType> {
    private final UserFollowRegistry registry;

    public FollowChooseState(
            UserTextMessageSender sender,
            UserFollowRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_STAGE_CHOOSE;
    }

    @Override
    public UserFollowOperationType getOption(String optionStr) {
        return UserFollowOperationType.valueOf(optionStr);
    }

    @Override
    public UserFollowOperationType[] getOptions(UserData userData) {
        return UserFollowOperationType.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, UserFollowOperationType t) {
        var data = registry.getOrCreate(user, UserFollowData::new);
        data.setType(t);
        return UserMenu.FOLLOW_STAGE_1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("%s <b>Выберите операцию</b>",
                DynamicEmoji.CHOOSE.getEmoji());
    }
}
