package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.WordsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.WordsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class WordsDeleteIdState extends AbstractTextState {
    private final WordsDeleteRegistry registry;

    public WordsDeleteIdState(
            UserTextMessageSender sender,
            WordsDeleteRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage() {
        return "Пожалуйста, введите words-ID слов, которые хотели бы удалить...";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS_REMOVE_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.WORDS;
        }

        String field = update.getMessage().getText();
        List<Long> ids = Arrays
                .stream(field.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(str -> {
                    try {
                        return Long.parseLong(str);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        var data = registry.getOrCreate(user, WordsDeleteData::new);
        data.setIds(ids);
        return UserMenu.WORDS_REMOVE_STAGE_P;
    }
}
