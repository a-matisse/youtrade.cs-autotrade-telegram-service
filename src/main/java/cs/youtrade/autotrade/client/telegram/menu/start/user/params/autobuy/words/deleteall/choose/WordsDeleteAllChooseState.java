package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.deleteall.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.AbstractWordsChooseState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.WordsType;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.deleteall.WordsDeleteAllData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.deleteall.WordsDeleteAllRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WordsDeleteAllChooseState extends AbstractWordsChooseState {
    private final WordsDeleteAllRegistry registry;

    public WordsDeleteAllChooseState(
            UserTextMessageSender sender,
            WordsDeleteAllRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS_REMOVE_ALL_STAGE_CHOOSE;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, WordsType t) {
        var data = registry.getOrCreate(userData, WordsDeleteAllData::new);
        data.setType(t);
        return UserMenu.WORDS_REMOVE_ALL_STAGE_P;
    }
}
