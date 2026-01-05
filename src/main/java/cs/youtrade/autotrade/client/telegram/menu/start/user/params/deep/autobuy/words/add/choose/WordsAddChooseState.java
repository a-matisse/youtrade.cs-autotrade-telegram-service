package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.words.add.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.words.AbstractWordsChooseState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.words.WordsType;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.words.add.WordsAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.words.add.WordsAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WordsAddChooseState extends AbstractWordsChooseState {
    private final WordsAddRegistry registry;

    public WordsAddChooseState(
            UserTextMessageSender sender,
            WordsAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS_ADD_STAGE_CHOOSE;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, WordsType t) {
        var data = registry.getOrCreate(userData, WordsAddData::new);
        data.setType(t);
        return UserMenu.WORDS_ADD_STAGE_1;
    }
}
