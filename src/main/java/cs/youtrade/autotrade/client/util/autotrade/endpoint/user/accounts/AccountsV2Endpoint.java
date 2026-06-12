package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdAccountsPageDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdCodeAnsDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.MaFileTokenAddInput;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountsV2Endpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdAccountsPageDto> getAccountsPage(
            Long chatId,
            Integer page,
            Integer size
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "page", page.toString(),
                "size", size.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint(),
                getHeaders(),
                params,
                new TypeToken<FcdAccountsPageDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdCodeAnsDto> addWorker(
            Long chatId,
            MaFileTokenAddInput input
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/worker"),
                getHeaders(),
                params,
                input,
                new TypeToken<FcdCodeAnsDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdCodeAnsDto> deleteBuyer(
            Long chatId,
            Long tokenId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "tokenId", tokenId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.DELETE,
                createEndpoint("/buyer"),
                getHeaders(),
                params,
                new TypeToken<FcdCodeAnsDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdCodeAnsDto> deleteSeller(
            Long chatId,
            Long tokenId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "tokenId", tokenId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.DELETE,
                createEndpoint("/seller"),
                getHeaders(),
                params,
                new TypeToken<FcdCodeAnsDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdCodeAnsDto> deleteWorker(
            Long chatId,
            Long tokenId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "tokenId", tokenId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.DELETE,
                createEndpoint("/worker"),
                getHeaders(),
                params,
                new TypeToken<FcdCodeAnsDto>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/accounts/v2";
    }
}
