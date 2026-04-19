package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractWordsChooseState extends YTPTextMenuState<WordsType> {
    public AbstractWordsChooseState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public WordsType getOption(String optionStr) {
        return WordsType.valueOf(optionStr);
    }

    @Override
    public WordsType[] getOptions(UserData userData) {
        return WordsType.values();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return "<b>Выберите тип слов для работы...</b>";
    }
}
