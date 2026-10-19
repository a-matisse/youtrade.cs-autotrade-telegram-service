package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.delete.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.delete.WordsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.delete.WordsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.WordDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.ExcludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.IncludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WordsDeleteIdState extends YTPTextState {
    private final WordsDeleteRegistry registry;
    private final IncludedWordsEndpoint inEndpoint;
    private final ExcludedWordsEndpoint exEndpoint;

    public WordsDeleteIdState(
            UserTextMessageSender sender,
            WordsDeleteRegistry registry,
            IncludedWordsEndpoint inEndpoint,
            ExcludedWordsEndpoint exEndpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.inEndpoint = inEndpoint;
        this.exEndpoint = exEndpoint;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.autobuy.UserWords");
        var deleteData = registry.getOrCreate(userData, WordsDeleteData::new);
        String typeHeader = switch (deleteData.getType()) {
            case INCLUDED -> String.format("%s <b>%s</b>",
                    DynamicEmoji.SUCCESS_2.getEmoji(),
                    translations.get("inclusion-keywords-2"));
            case EXCLUDED -> String.format("%s <b>%s</b>",
                    DynamicEmoji.PROHIBIT.getEmoji(),
                    translations.get("exclusion-keywords-2"));
            case RETURN -> null;
        };
        if (typeHeader == null)
            return null;

        return String.format("""
                        %s
                        <blockquote expandable>%s</blockquote>
                        
                        %s
                        """,
                        typeHeader,
                        getScoringIdMes(userData),
                        translations.format("enter-the-keyword-ids-b-you-want", Map.of("emphasis", String.format("<b>%s</b>", translations.get("enter-the-keyword-ids-b-you-want.emphasis")))));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS_REMOVE_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.UserWords");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
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
        var translations = botTexts.forUser(user, "parameters.autobuy.UserWords");
        var data = registry.getOrCreate(user, WordsDeleteData::new);
        var type = data.getType();
        AbstractAtWordsEndpoint endpoint = switch (type) {
            case INCLUDED -> inEndpoint;
            case EXCLUDED -> exEndpoint;
            case RETURN -> null;
        };
        if (endpoint == null)
            return null;

        var restAns = endpoint.wordsGet(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var words = fcd.getData();
        if (words.isEmpty())
            return String.format("<b>🚫 %s</b>",
                    translations.get("the-keyword-list-is-empty"));

        return words
                .stream()
                .map(WordDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
