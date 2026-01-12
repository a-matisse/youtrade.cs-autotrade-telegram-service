package cs.youtrade.autotrade.client.telegram.menu.start.user.follow.follow.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.follow.follow.UserFollowData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.follow.follow.UserFollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class FollowIdState extends AbstractTextState {
    private final UserFollowRegistry registry;

    public FollowIdState(
            UserTextMessageSender sender,
            UserFollowRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return "Пожалуйста, введите params-ID, с которым хотели бы работать...";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.WORDS;
        }

        String input = update.getMessage().getText();
        long paramsId;
        try {
            paramsId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, UserFollowData::new);
        data.setYourTdpId(paramsId);
        return UserMenu.FOLLOW_STAGE_2;
    }
}
