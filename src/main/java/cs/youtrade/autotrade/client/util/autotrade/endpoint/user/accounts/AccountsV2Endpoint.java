package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdAccountsV2Dto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdCodeAnsDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdCodeBulkAnswer;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.MaFileTokenAddInput;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import cs.youtrade.ytrest.util.YtMultiMap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccountsV2Endpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdAccountsV2Dto> getAccountsPage(
            Long chatId,
            Integer page,
            Integer size
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "page", page.toString(),
                "size", size.toString()
        );
        return client.fetchFromApi(HttpMethod.GET, createEndpoint())
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdAccountsV2Dto>() {
                }.getType())
                .build()
                .fetch();
    }

    public RestAnswer<FcdCodeAnsDto> addWorker(
            Long chatId,
            MaFileTokenAddInput input
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/worker"))
                .headers(getHeaders())
                .params(params)
                .body(input)
                .type(new TypeToken<FcdCodeAnsDto>() {
                }.getType())
                .build()
                .fetch();
    }

    public RestAnswer<FcdCodeBulkAnswer> deleteBuyer(
            Long chatId,
            List<Long> tokenIds
    ) {
        // Creating params
        YtMultiMap<String, String> params = new YtMultiMap<>();
        params.add("chatId", chatId.toString());
        var tokenIdStrs = tokenIds.stream().map(Object::toString).toList();
        params.addAll("tokenIds", tokenIdStrs);
        // Executing the request
        return client.fetchFromApi(HttpMethod.DELETE, createEndpoint("/buyer"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdCodeBulkAnswer>() {
                }.getType())
                .build()
                .fetch();
    }

    public RestAnswer<FcdCodeBulkAnswer> deleteSeller(
            Long chatId,
            List<Long> tokenIds
    ) {
        // Creating params
        YtMultiMap<String, String> params = new YtMultiMap<>();
        params.add("chatId", chatId.toString());
        var tokenIdStrs = tokenIds.stream().map(Object::toString).toList();
        params.addAll("tokenIds", tokenIdStrs);
        // Executing the request
        return client.fetchFromApi(HttpMethod.DELETE, createEndpoint("/seller"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdCodeBulkAnswer>() {
                }.getType())
                .build()
                .fetch();
    }

    public RestAnswer<FcdCodeBulkAnswer> deleteWorker(
            Long chatId,
            List<Long> tokenIds
    ) {
        // Creating params
        YtMultiMap<String, String> params = new YtMultiMap<>();
        params.add("chatId", chatId.toString());
        var tokenIdStrs = tokenIds.stream().map(Object::toString).toList();
        params.addAll("tokenIds", tokenIdStrs);
        // Executing the request
        return client.fetchFromApi(HttpMethod.DELETE, createEndpoint("/worker"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdCodeBulkAnswer>() {
                }.getType())
                .build()
                .fetch();
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/accounts/v2";
    }
}
