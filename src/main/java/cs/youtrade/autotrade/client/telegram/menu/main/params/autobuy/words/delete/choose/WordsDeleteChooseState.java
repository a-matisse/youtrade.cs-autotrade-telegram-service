package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.AbstractWordsChooseState;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.WordsType;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.WordsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.delete.WordsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WordsDeleteChooseState extends AbstractWordsChooseState {
    private final WordsDeleteRegistry registry;

    public WordsDeleteChooseState(
            UserTextMessageSender sender,
            WordsDeleteRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS_REMOVE_STAGE_CHOOSE;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, WordsType t) {
        var data = registry.getOrCreate(userData, WordsDeleteData::new);
        data.setType(t);
        return UserMenu.WORDS_REMOVE_STAGE_1;
    }
}
