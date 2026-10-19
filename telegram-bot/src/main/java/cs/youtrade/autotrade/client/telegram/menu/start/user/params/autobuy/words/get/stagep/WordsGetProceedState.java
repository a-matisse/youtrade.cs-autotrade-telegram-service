package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.get.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.get.WordsGetRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.WordDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.ExcludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.IncludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordsGetProceedState extends YTPTerminalTextMenuState {
    private final WordsGetRegistry registry;
    private final IncludedWordsEndpoint inEndpoint;
    private final ExcludedWordsEndpoint exEndpoint;

    public WordsGetProceedState(
            UserTextMessageSender sender,
            WordsGetRegistry registry,
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
    public UserMenu supportedState() {
        return UserMenu.WORDS_GET_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.UserWords");
        var data = registry.remove(user);
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
            return String.format("%s <b>%s</b>",
                    DynamicEmoji.ERROR.getEmoji(),
                    translations.get("the-keyword-list-is-empty"));

        String typeHeader = switch (type) {
            case INCLUDED -> String.format("%s <b>%s</b>",
                    DynamicEmoji.SUCCESS_2.getEmoji(),
                    translations.get("inclusion-keywords-2"));
            case EXCLUDED -> String.format("%s <b>%s</b>",
                    DynamicEmoji.PROHIBIT.getEmoji(),
                    translations.get("exclusion-keywords-2"));
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        return String.format("""
                        %s
                        <blockquote expandable>%s</blockquote>
                        """,
                typeHeader,
                getWordsStr(words)
        );
    }

    @Override
    public UserMenu retState() {
        return UserMenu.WORDS;
    }


    private String getWordsStr(List<WordDto> words) {
        return words
                .stream()
                .map(WordDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
