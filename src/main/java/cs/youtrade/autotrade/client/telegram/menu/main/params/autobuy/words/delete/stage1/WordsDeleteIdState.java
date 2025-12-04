package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.WordsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.WordsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.WordDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.ExcludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.IncludedWordsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WordsDeleteIdState extends AbstractTextState {
    private final WordsDeleteRegistry registry;
    private final IncludedWordsEndpoint inEndpoint;
    private final ExcludedWordsEndpoint exEndpoint;

    public WordsDeleteIdState(
            UserTextMessageSender sender,
            WordsDeleteRegistry registry,
            IncludedWordsEndpoint inEndpoint,
            ExcludedWordsEndpoint exEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.inEndpoint = inEndpoint;
        this.exEndpoint = exEndpoint;
    }

    @Override
    protected String getMessage(UserData user) {
        return String.format("""
                        Список ваших words-ID (%s):
                        %s
                        
                        Пожалуйста, введите words-ID слов, которые хотели бы удалить...
                        """,
                registry.get(user).getType(),
                getScoringIdMes(user)
        );
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

    private String getScoringIdMes(UserData user) {
        var data = registry.get(user);
        var type = data.getType();
        AbstractAtWordsEndpoint endpoint = switch (type) {
            case INCLUDED -> inEndpoint;
            case EXCLUDED -> exEndpoint;
        };

        var restAns = endpoint.wordsGet(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var words = fcd.getData();
        if (words.isEmpty())
            return "Список слов пуст";

        return words
                .stream()
                .map(WordDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
