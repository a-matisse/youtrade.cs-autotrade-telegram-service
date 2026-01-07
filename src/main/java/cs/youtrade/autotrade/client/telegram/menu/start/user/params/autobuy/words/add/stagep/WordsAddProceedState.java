package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.add.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.add.WordsAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.ExcludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.IncludedWordsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WordsAddProceedState extends AbstractTerminalTextMenuState {
    private final WordsAddRegistry registry;
    private final IncludedWordsEndpoint inEndpoint;
    private final ExcludedWordsEndpoint exEndpoint;

    public WordsAddProceedState(
            UserTextMessageSender sender,
            WordsAddRegistry registry,
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
        return UserMenu.WORDS_ADD_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        var type = data.getType();
        AbstractAtWordsEndpoint endpoint = switch (type) {
            case INCLUDED -> inEndpoint;
            case EXCLUDED -> exEndpoint;
        };

        var restAns = endpoint.wordsAdd(user.getChatId(), data.getKeyWord());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var added = fcd.getAdded();
        var skipped = fcd.getSkipped();
        StringBuilder response = new StringBuilder();
        if (!added.isEmpty()) {
            response.append(String.format("Слова успешно добавлены (%s): ", type.getButtonName()))
                    .append(String.join(", ", added))
                    .append(".\n");
        }
        if (!skipped.isEmpty()) {
            response.append("Не были добавлены (уже существовали): ")
                    .append(String.join(", ", skipped))
                    .append(".");
        }

        return response.toString().trim();
    }

    @Override
    public UserMenu retState() {
        return UserMenu.WORDS;
    }
}
