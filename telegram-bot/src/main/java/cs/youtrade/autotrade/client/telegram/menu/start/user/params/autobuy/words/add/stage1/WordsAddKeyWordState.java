package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.add.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.add.WordsAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.add.WordsAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.List;

@Service
public class WordsAddKeyWordState extends YTPTextState {
    private final WordsAddRegistry registry;

    public WordsAddKeyWordState(
            UserTextMessageSender sender,
            WordsAddRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.autobuy.UserWords");
        return String.format("%s <b>%s</b>",
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("enter-exclusion-keywords-separated-by"));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.WORDS_ADD_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.UserWords");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.WORDS;
        }

        String field = update.getMessage().getText();
        List<String> separated = Arrays
                .stream(field.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        var data = registry.getOrCreate(user, WordsAddData::new);
        data.setKeyWord(separated);
        return UserMenu.WORDS_ADD_STAGE_P;
    }
}
