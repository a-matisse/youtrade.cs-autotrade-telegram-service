package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractPcoTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.ExcludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts.IncludedWordsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
public class UserWordsState extends AbstractPcoTextMenuState<UserWordsMenu> {
    private final ParamsEndpoint endpoint;
    private final ExcludedWordsEndpoint exWEndpoint;
    private final IncludedWordsEndpoint inWEndpoint;

    public UserWordsState(
            UserTextMessageSender sender,
            ParamsEndpoint endpoint,
            ExcludedWordsEndpoint exWEndpoint,
            IncludedWordsEndpoint inWEndpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.endpoint = endpoint;
        this.exWEndpoint = exWEndpoint;
        this.inWEndpoint = inWEndpoint;
    }

    @Override
    public List<ParamsCopyOptions> getMenuPcos() {
        return List.of(ParamsCopyOptions.EXCLUDED_WORDS, ParamsCopyOptions.INCLUDED_WORDS);
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
    public UserWordsMenu[] getOptions(UserData userData) {
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
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.UserWords");
        return String.format("""
                        <tg-emoji emoji-id="5226512880362332956">📖</tg-emoji> <b>%s</b>
                        <blockquote>%s
                        %s</blockquote>
                        
                        %s
                        """,
                        translations.get("keyword-list-size"),
                        getIncludedCount(user),
                        getExcludedCount(user),
                        getFollowText(user));
    }

    private String getFollowText(UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.UserWords");
        var restAns = endpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return getFollowWorks(fcd.getData(), translations.inBundle("parameters.follow.AbstractPco"));
    }

    private String getExcludedCount(UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.UserWords");
        return String.format("• %s: <b>%d</b>",
                translations.get("exclusion-keywords"),
                getCount(exWEndpoint, user));
    }

    private String getIncludedCount(UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.UserWords");
        return String.format("• %s: <b>%d</b>",
                translations.get("inclusion-keywords"),
                getCount(inWEndpoint, user));
    }

    private Long getCount(AbstractAtWordsEndpoint endpoint, UserData user) {
        var restAns = endpoint.wordsCount(user.getChatId());
        if (restAns.getStatus() >= 300)
            return -1L;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return -1L;

        return fcd.getData();
    }
}
