package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.UserAccountsMetaData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdAccountsPageV2Dto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserAccountsState extends YTPTextMenuState<UserAccountsMenu> {
    private static final Map<Long, UserAccountsMetaData> USER_ACCOUNTS_CACHE = new ConcurrentHashMap<>();

    private final AccountsV2Endpoint endpoint;
    private final ParamsEndpoint paramsEndpoint;

    public UserAccountsState(
            UserTextMessageSender sender,
            AccountsV2Endpoint endpoint,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
        this.paramsEndpoint = paramsEndpoint;
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
            case ACCOUNTS_PREVIOUS -> computeInnerButtons(userData, UserAccountsMetaData::decrementPage);
            case ACCOUNTS_MODE -> computeInnerButtons(userData, UserAccountsMetaData::switchMode);
            case ACCOUNTS_NEXT -> computeInnerButtons(userData, UserAccountsMetaData::incrementPage);
            case ACCOUNTS_ADD -> UserMenu.ACCOUNTS_ADD_STAGE_CHOOSE;
            case ACCOUNTS_REMOVE -> UserMenu.ACCOUNTS_REMOVE_STAGE_CHOOSE;
            case ACCOUNTS_RENAME -> UserMenu.ACCOUNTS_RENAME_STAGE_1;
            case RETURN -> UserMenu.USER;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        // Getting sell path data
        var pathAns = paramsEndpoint.getCurrent(chatId);
        if (pathAns.getStatus() >= 300)
            return null;
        var pathFcd = pathAns.getResponse();
        if (!pathFcd.isResult())
            return pathFcd.getCause();
        var pathData = pathFcd.getData();
        // Getting user tokenData page
        var pageData = getUserAccountsData(chatId, pathData);
        var restAns = endpoint.getAccountsPage(chatId, pageData.getPage(), pageData.getSize());
        if (restAns.getStatus() >= 300)
            return null;
        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();
        // Returning the message
        fcd = pageData.setPageMetadata(fcd);
        var tokenListStr = getTokenListStr(pageData, fcd.getAccounts());
        int page = pageData.getPage() + 1;
        int totalPages = fcd.getAccounts().getTotalPages();
        return String.format("""
                        %s <i>Управление аккаунтами (%s/%s)</i>
                        
                        %s <b>Аккаунты</b>
                        <blockquote expandable>%s</blockquote>
                        
                        %s
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(), page, totalPages,
                // ToDo: Аккаунты
                DynamicEmoji.STEAM.getEmoji(), tokenListStr,
                pathData.getDirection()
        );
    }

    private String getTokenListStr(UserAccountsMetaData data, FcdAccountsPageV2Dto accounts) {
        if (accounts.isEmpty())
            return String.format("%s Список аккаунтов пуст",
                    DynamicEmoji.ERROR.getEmoji());

        return accounts
                .stream()
                .sorted(Comparator.comparingLong(FcdAccountV2Dto::getId))
                .map(account -> account.asMessage(data))
                .collect(Collectors.joining("\n"))
                .trim();
    }

    private UserAccountsMetaData getUserAccountsData(long chatId, FcdParamsGetDto ydp) {
        return USER_ACCOUNTS_CACHE.computeIfAbsent(chatId, id -> new UserAccountsMetaData(id, ydp));
    }

    private UserAccountsMetaData getUserAccountsData(long chatId) {
        return USER_ACCOUNTS_CACHE.get(chatId);
    }

    private UserMenu computeInnerButtons(UserData userData, Consumer<UserAccountsMetaData> metaDataConsumer) {
        var pageData = getUserAccountsData(userData.getChatId());
        metaDataConsumer.accept(pageData);
        return UserMenu.ACCOUNTS;
    }

    @Override
    public Map<UserAccountsMenu, Predicate<UserData>> getVisibilityPredicates(UserData userData) {
        var pageData = getUserAccountsData(userData.getChatId());
        return Map.of(
                UserAccountsMenu.ACCOUNTS_PREVIOUS, _ -> pageData.hasPrevious(),
                UserAccountsMenu.ACCOUNTS_NEXT, _ -> pageData.hasNext()
        );
    }

    @Override
    public Map<UserAccountsMenu, Function<UserData, String>> getTextFunctions(UserData userData) {
        var mode = getUserAccountsData(userData.getChatId()).getMode();
        return Map.of(
                UserAccountsMenu.ACCOUNTS_MODE, _ ->
                        "Режим: " + mode.getEmoji() + " " + mode.getRussianName()
        );
    }
}
