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
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/token"))
                .headers(getHeaders())
                .body(dto)
                .type(new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<Integer>> tokenDelete(
            Long chatId,
            Long tokenId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "tokenId", tokenId.toString()
        );
        return client.fetchFromApi(HttpMethod.DELETE, createEndpoint("/token"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Integer>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<Integer>> tokenDeleteAll(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.DELETE, createEndpoint("/token/all"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Integer>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<Boolean>> toggle(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/toggle"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<Boolean>> toggleBargainable(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/toggle/bargainable"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<DuplicateMode>> switchDuplicateMode(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/switch/duplicate-mode"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<DuplicateMode>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<FunctionType>> switchFunctionType(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/switch/function-type"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<FunctionType>>() {
                }.getType()).build().fetch();
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/buy";
    }
}
