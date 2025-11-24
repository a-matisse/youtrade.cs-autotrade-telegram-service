package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.get.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.AbstractWordsChooseState;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.WordsType;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.get.WordsGetData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.get.WordsGetRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WordsGetChooseState extends AbstractWordsChooseState {
    private final WordsGetRegistry registry;

    public WordsGetChooseState(
            UserTextMessageSender sender,
            WordsGetRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS_GET_STAGE_CHOOSE;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, WordsType t) {
        var data = registry.getOrCreate(user, WordsGetData::new);
        data.setType(t);
        return UserMenu.WORDS_GET_STAGE_P;
    }
}
