package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.ParamsAddDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.*;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ParamsEndpoint extends AbstractAtEndpoint {
    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/params";
    }

    public RestAnswer<FcdDefaultDto<Long>> create(
            Long chatId,
            MarketType source,
            MarketType destination,
            String token
    ) {
        ParamsAddDto dto = new ParamsAddDto(chatId, source, destination, token);
        return client.fetchFromApi(HttpMethod.POST, createEndpoint())
                .headers(getHeaders())
                .body(dto)
                .type(new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<FcdParamsGetDto>> getCurrent(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.GET, createEndpoint())
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<FcdParamsGetDto>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<List<FcdParamsListDto>>> listParams(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.GET, createEndpoint("/all"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<List<FcdParamsListDto>>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdParamsCopyReqDto> requestCopy(
            Long thatChatId,
            Long yourTdpId,
            ParamsCopyOptions pco
    ) {
        Map<String, String> params = Map.of(
                "thatChatId", thatChatId.toString(),
                "yourTdpId", yourTdpId.toString(),
                "pco", pco.name()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/copy"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdParamsCopyReqDto>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdParamsCopyResDto> proceedCopy(
            String callback
    ) {
        Map<String, String> params = Map.of(
                "callback", callback
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/copy/proceed"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdParamsCopyResDto>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdParamsCopyReqDto> requestFollow(
            Long thatChatId,
            Long yourTdpId,
            ParamsCopyOptions pco
    ) {
        Map<String, String> params = Map.of(
                "thatChatId", thatChatId.toString(),
                "yourTdpId", yourTdpId.toString(),
                "pco", pco.name()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/follow"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdParamsCopyReqDto>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdParamsCopyResDto> proceedFollow(
            String callback
    ) {
        Map<String, String> params = Map.of(
                "callback", callback
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/follow/proceed"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdParamsCopyResDto>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<Long>> unfollow(
            Long chatId,
            Long followId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "followId", followId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/unfollow"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdParamsDeleteReqDto> requestDelete(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/delete/request"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdParamsDeleteReqDto>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdParamsDeleteResDto> proceedDelete(
            Long chatId,
            String msgData
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "msgData", msgData
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/delete/proceed"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdParamsDeleteResDto>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<FcdParamsSwitchDto>> switchP(
            Long chatId,
            String input
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "input", input
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/switch"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<FcdParamsSwitchDto>>() {
                }.getType()).build().fetch();
    }
}
