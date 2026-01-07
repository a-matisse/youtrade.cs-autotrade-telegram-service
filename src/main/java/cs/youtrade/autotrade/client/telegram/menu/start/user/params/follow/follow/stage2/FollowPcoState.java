package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.UserFollowData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.follow.UserFollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Objects;

@Service
public class FollowPcoState extends AbstractTextState {
    private final UserFollowRegistry registry;

    public FollowPcoState(
            UserTextMessageSender sender,
            UserFollowRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return String.format("""
                        📋 Выберите режим копирования:
                        
                        %s
                        
                        Введите номер (0-7) или название режима:
                        """,
                ParamsCopyOptions.generateDescription()
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
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
                sender.sendTextMes(bot, chatId, "#1: настройки копирования не найдены. Возвращение обратно...");
                return UserMenu.FOLLOW;
            }
        }
        var data = registry.getOrCreate(user, UserFollowData::new);
        data.setPco(pco);
        return UserMenu.FOLLOW_STAGE_P;
    }
}
