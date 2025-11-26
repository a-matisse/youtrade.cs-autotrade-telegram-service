package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.follow.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.follow.UserFollowData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.follow.UserFollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqDto;
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
    protected String getMessage() {
        return """
                📋 Выберите режим копирования:
                
                0️⃣ FULL - Полное копирование параметров
                1️⃣ WORDS - Основные настройки параметров
                2️⃣ EXCLUDED_WORDS - Только исключаемые слова
                3️⃣ INCLUDED_WORDS - Только включаемые слова
                4️⃣ MAIN_ONLY - Основные настройки параметров
                5️⃣ AUTOBUY - Основные настройки автопокупки
                6️⃣ AUTOSELL - Основные настройки автопродажи
                7️⃣ SCORING - Все profit-id
                
                Введите номер (0-7) или название режима:
                """;
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
