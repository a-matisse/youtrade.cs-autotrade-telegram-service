package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;

public abstract class AbstractWordsChooseState extends AbstractTextMenuState<WordsType> {
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
    public WordsType[] getOptions() {
        return WordsType.values();
    }

    @Override
    public String getHeaderText(UserData userData) {
        return "Выберите тип слов для работы...";
    }
}
