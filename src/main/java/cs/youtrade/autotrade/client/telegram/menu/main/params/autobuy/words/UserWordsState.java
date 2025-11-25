package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserWordsState extends AbstractTextMenuState<UserWordsMenu> {
    public UserWordsState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS;
    }

    @Override
    public UserWordsMenu getOption(String optionStr) {
        return UserWordsMenu.valueOf(optionStr);
    }

    @Override
    public UserWordsMenu[] getOptions() {
        return UserWordsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserWordsMenu t) {
        return switch (t) {
            case WORDS_GET -> UserMenu.WORDS_GET_STAGE_CHOOSE;
            case WORDS_ADD -> UserMenu.WORDS_ADD_STAGE_CHOOSE;
            case WORDS_DELETE -> UserMenu.WORDS_REMOVE_STAGE_CHOOSE;
            case WORDS_DELETE_ALL -> UserMenu.WORDS_REMOVE_ALL_STAGE_CHOOSE;
            case RETURN -> UserMenu.AUTOBUY;
        };
    }

    @Override
    public String getHeaderText(UserData userData) {
        return "📚 Раздел управления словарем";
    }
}
