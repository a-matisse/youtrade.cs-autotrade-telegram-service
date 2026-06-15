package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class YTPPageProcessor {
    private final Map<Long, UserAccountsMetaData> USER_ACCOUNTS_CACHE = new ConcurrentHashMap<>();
    private final Map<Long, String> USER_LAST_ERROR_CACHE = new ConcurrentHashMap<>();

    private final AccountsV2Endpoint endpoint;
    private final ParamsEndpoint paramsEndpoint;

    public YTPAccountsPageProcessorDto getPage(long chatId) throws RuntimeException {
        // Getting sell path data
        var pathAns = paramsEndpoint.getCurrent(chatId);
        if (pathAns.getStatus() >= 300) {
            String err = "Cannot access params: " + pathAns.getStatus();
            onError(chatId, err);
        }
        var pathFcd = pathAns.getResponse();
        if (!pathFcd.isResult()) {
            String err = pathFcd.getCause();
            onError(chatId, err);
        }
        var params = pathFcd.getData();
        // Getting user tokenData page
        var pageData = getUserAccountsData(chatId, params);
        var restAns = endpoint.getAccountsPage(chatId, pageData.getPage(), pageData.getSize());
        if (restAns.getStatus() >= 300) {
            String err = "Cannot access fcd: " + restAns.getStatus();
            onError(chatId, err);
        }
        var fcd = restAns.getResponse();
        if (!fcd.isResult()) {
            String err = fcd.getCause();
            onError(chatId, err);
        }
        // Returning the message
        fcd = pageData.setPageMetadata(fcd);
        return new YTPAccountsPageProcessorDto(params, fcd, pageData);
    }

    // --- Page methods
    public void incrementPage(long chatId) {
        var pageData = getUserAccountsData(chatId);
        pageData.incrementPage();
    }

    public void decrementPage(long chatId) {
        var pageData = getUserAccountsData(chatId);
        pageData.decrementPage();
    }

    public boolean hasNextPage(long chatId) {
        var pageData = getUserAccountsData(chatId);
        return pageData.hasNext();
    }

    public boolean hasPreviousPage(long chatId) {
        var pageData = getUserAccountsData(chatId);
        return pageData.hasPrevious();
    }

    // --- Mode methods
    public UserAccountsMode getMode(long chatId) {
        var pageData = getUserAccountsData(chatId);
        return pageData.getMode();
    }

    public void switchMode(long chatId) {
        var pageData = getUserAccountsData(chatId);
        pageData.switchMode();
    }

    // --- Assistive methods
    public String getLastError(long chatId) {
        return USER_LAST_ERROR_CACHE.get(chatId);
    }

    private void onError(long chatId, String err) {
        USER_LAST_ERROR_CACHE.put(chatId, err);
        throw new RuntimeException(err);
    }

    private UserAccountsMetaData getUserAccountsData(long chatId, FcdParamsGetDto ydp) {
        return USER_ACCOUNTS_CACHE.computeIfAbsent(chatId, id -> new UserAccountsMetaData(id, ydp));
    }

    private UserAccountsMetaData getUserAccountsData(long chatId) {
        return USER_ACCOUNTS_CACHE.get(chatId);
    }
}
