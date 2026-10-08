package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class UserAccountsState extends AbstractUserAccountsState<UserAccountsMenu> {
    public UserAccountsState(
            UserTextMessageSender sender,
            YTPPageProcessor pageProcessor
    ) {
        super(sender, pageProcessor);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public UserAccountsMenu getOption(String optionStr) {
        return UserAccountsMenu.valueOf(optionStr);
    }

    @Override
    public UserAccountsMenu[] getOptions(UserData userData) {
        return UserAccountsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserAccountsMenu t) {
        return switch (t) {
            case ACCOUNTS_PREVIOUS -> computeInnerButtons(userData, pageProcessor::decrementPage);
            case ACCOUNTS_MODE -> computeInnerButtons(userData, pageProcessor::switchMode);
            case ACCOUNTS_NEXT -> computeInnerButtons(userData, pageProcessor::incrementPage);
            case ACCOUNTS_ADD -> UserMenu.ACCOUNTS_ADD_STAGE_CHOOSE;
            case ACCOUNTS_COLLECT -> UserMenu.ACCOUNTS_COLLECT;
            case ACCOUNTS_SEND -> UserMenu.ACCOUNTS_SEND;
            case ACCOUNTS_MORE -> UserMenu.ACCOUNTS_MORE;
            case RETURN -> UserMenu.USER;
        };
    }

    private UserMenu computeInnerButtons(UserData userData, Consumer<Long> metaDataConsumer) {
        metaDataConsumer.accept(userData.getChatId());
        return UserMenu.ACCOUNTS;
    }

    @Override
    public Map<UserAccountsMenu, Predicate<UserData>> getVisibilityPredicates(UserData userData) {
        long chatId = userData.getChatId();
        return Map.of(
                UserAccountsMenu.ACCOUNTS_PREVIOUS, _ -> pageProcessor.hasPreviousPage(chatId),
                UserAccountsMenu.ACCOUNTS_NEXT, _ -> pageProcessor.hasNextPage(chatId)
        );
    }

    @Override
    public Map<UserAccountsMenu, Function<UserData, String>> getTextFunctions(UserData userData) {
        var mode = pageProcessor.getMode(userData.getChatId());
        return Map.of(
                UserAccountsMenu.ACCOUNTS_MODE, _ ->
                        "Режим: " + mode.getEmoji() + " " + mode.getRussianName()
        );
    }
}
