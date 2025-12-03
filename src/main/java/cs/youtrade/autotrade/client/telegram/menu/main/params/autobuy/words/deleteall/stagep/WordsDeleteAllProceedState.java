package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.deleteall.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.deleteall.WordsDeleteAllRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.ExcludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.IncludedWordsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WordsDeleteAllProceedState extends AbstractTerminalTextMenuState {
    private final WordsDeleteAllRegistry registry;
    private final IncludedWordsEndpoint inEndpoint;
    private final ExcludedWordsEndpoint exEndpoint;

    public WordsDeleteAllProceedState(
            UserTextMessageSender sender,
            WordsDeleteAllRegistry registry,
            IncludedWordsEndpoint inEndpoint,
            ExcludedWordsEndpoint exEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.inEndpoint = inEndpoint;
        this.exEndpoint = exEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS_REMOVE_ALL_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        var type = data.getType();
        AbstractAtWordsEndpoint endpoint = switch (type) {
            case INCLUDED -> inEndpoint;
            case EXCLUDED -> exEndpoint;
        };

        var restAns = endpoint.deleteAllWords(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        int count = fcd.getData();
        return count > 0
                ? String.format("✅ Успешно удалено слов: %d", count)
                : "[2] ❌ Не найдены слова с соответствующими id";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.WORDS;
    }
}
