package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.stage2;

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
            UserFollowRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Пожалуйста, введите номер или название режима из списка...</b>
                        <blockquote>%s</blockquote>
                        """,
                DynamicEmoji.WRITE.getEmoji(),
                ParamsCopyOptions.generateDescription()
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
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
                sender.sendTextMes(bot, user, "#1: настройки копирования не найдены. Возвращение обратно...");
                return UserMenu.FOLLOW;
            }
        }
        var data = registry.getOrCreate(user, UserFollowData::new);
        data.setPco(pco);
        return UserMenu.FOLLOW_STAGE_P;
    }
}
