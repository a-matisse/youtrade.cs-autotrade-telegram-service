package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.DuplicateMode;
import cs.youtrade.autotrade.client.util.autotrade.FunctionType;
import cs.youtrade.autotrade.client.util.autotrade.dto.BuyTokenAddDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BuyEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<Long>> buyTokensAdd(
            long chatId,
            String api,
            String partnerId,
            String steamToken
    ) {
        BuyTokenAddDto dto = new BuyTokenAddDto(chatId, api, partnerId, steamToken);
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/token"),
                getHeaders(),
                dto,
                new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Integer>> tokenDelete(
            Long chatId,
            Long tokenId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "tokenId", tokenId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.DELETE,
                createEndpoint("/token"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<Integer>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Integer>> tokenDeleteAll(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.DELETE,
                createEndpoint("/token/all"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<Integer>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Boolean>> toggle(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/toggle"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<DuplicateMode>> switchDuplicateMode(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/switch/duplicate-mode"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<DuplicateMode>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<FunctionType>> switchFunctionType(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/switch/function-type"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<FunctionType>>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/buy";
    }
}
