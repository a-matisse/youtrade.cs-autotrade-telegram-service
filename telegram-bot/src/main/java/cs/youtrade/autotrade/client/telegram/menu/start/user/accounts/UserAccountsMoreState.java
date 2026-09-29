package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.function.Function;

@Service
public class UserAccountsMoreState extends AbstractUserAccountsState<UserAccountsMoreMenu> {
    public UserAccountsMoreState(UserTextMessageSender sender, YTPPageProcessor pageProcessor) {
        super(sender, pageProcessor);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_MORE;
    }

    @Override
    public UserAccountsMoreMenu getOption(String optionStr) {
        return UserAccountsMoreMenu.valueOf(optionStr);
    }

    @Override
    public UserAccountsMoreMenu[] getOptions(UserData userData) {
        return UserAccountsMoreMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserAccountsMoreMenu t) {
        return switch (t) {
            case RENAME -> UserMenu.ACCOUNTS_RENAME_STAGE_1;
            case REMOVE -> UserMenu.ACCOUNTS_REMOVE_STAGE_CHOOSE;
            case TRANSFER -> UserMenu.ACCOUNTS_TRANSFER_STAGE_1;
            case ACCOUNTS_MODE -> {
                pageProcessor.switchMode(userData.getChatId());
                yield UserMenu.ACCOUNTS_MORE;
            }
            case BACK -> UserMenu.ACCOUNTS;
            case RETURN -> UserMenu.USER;
        };
    }

    @Override
    public Map<UserAccountsMoreMenu, Function<UserData, String>> getTextFunctions(UserData userData) {
        var mode = pageProcessor.getMode(userData.getChatId());
        return Map.of(
                UserAccountsMoreMenu.ACCOUNTS_MODE, _ ->
                        "Режим: " + mode.getEmoji() + " " + mode.getRussianName()
        );
    }
}
