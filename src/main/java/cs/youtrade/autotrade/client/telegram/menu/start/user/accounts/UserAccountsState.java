package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@Log4j2
public class UserAccountsState extends YTPTextMenuState<UserAccountsMenu> {
    private final YTPPageProcessor pageProcessor;

    public UserAccountsState(
            UserTextMessageSender sender,
            AccountsV2Endpoint endpoint,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.pageProcessor = new YTPPageProcessor(endpoint, paramsEndpoint);
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
            case ACCOUNTS_REMOVE -> UserMenu.ACCOUNTS_REMOVE_STAGE_CHOOSE;
            case ACCOUNTS_RENAME -> UserMenu.ACCOUNTS_RENAME_STAGE_1;
            case RETURN -> UserMenu.USER;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        try {
            // Returning the message
            var dto = pageProcessor.getPage(chatId);
            var params = dto.params();
            var fcd = dto.fcd();
            var pageData = dto.pageData();
            var tokenListStr = dto.getAccountsListStr();
            int page = pageData.getPage() + 1;
            int totalPages = fcd.getAccounts().getTotalPages();
            return String.format("""
                            %s <i>Управление аккаунтами (%s/%s)</i>
                            
                            %s <b>Аккаунты</b>
                            <blockquote expandable>%s</blockquote>
                            
                            %s
                            """,
                    DynamicEmoji.YOUTRADE.getEmoji(), page, totalPages,
                    DynamicEmoji.STEAM.getEmoji(), tokenListStr,
                    params.getDirection()
            );
        } catch (RuntimeException e) {
            // Catching the error and sending the user
            log.error(e);
            return pageProcessor.getLastError(chatId);
        }
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
