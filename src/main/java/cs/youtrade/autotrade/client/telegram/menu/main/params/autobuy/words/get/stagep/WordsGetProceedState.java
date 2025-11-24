package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.get.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.get.WordsGetRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.ExcludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.IncludedWordsEndpoint;
import org.springframework.stereotype.Service;

@Service
public class WordsGetProceedState extends AbstractTerminalTextMenuState {
    private final WordsGetRegistry registry;
    private final IncludedWordsEndpoint inEndpoint;
    private final ExcludedWordsEndpoint exEndpoint;

    public WordsGetProceedState(
            UserTextMessageSender sender,
            WordsGetRegistry registry,
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
        return UserMenu.WORDS_GET_STAGE_P;
    }

    @Override
    public String getHeaderText(UserData userData) {
        return "";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.WORDS;
    }
}
