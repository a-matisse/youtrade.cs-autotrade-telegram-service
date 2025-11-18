package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.ParamsAddDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.*;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public class ParamsEndpoint extends AbstractAtEndpoint {
    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/params";
    }

    public RestAnswer<FcdDefaultDto<Long>> create(
            Long chatId,
            MarketType source,
            String token
    ) {
        ParamsAddDto dto = new ParamsAddDto(chatId, source, token);
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                dto,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<FcdParamsGetDto>> getCurrent(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint(),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<List<FcdParamsListDto>>> listParams(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint("/all"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
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
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/copy"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdParamsCopyResDto> proceedCopy(
            String callback
    ) {
        Map<String, String> params = Map.of(
                "callback", callback
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/copy/proceed"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
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
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/follow"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdParamsCopyResDto> proceedFollow(
            String callback
    ) {
        Map<String, String> params = Map.of(
                "callback", callback
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/follow/proceed"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<Long>> unfollow(
            Long chatId,
            Long followId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "followId", followId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/unfollow"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdParamsDeleteReqDto> requestDelete(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/delete/request"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdParamsDeleteResDto> proceedDelete(
            Long chatId,
            String msgData
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "msgData", msgData
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/delete/proceed"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<FcdParamsSwitchDto>> switchP(
            Long chatId,
            String input
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "input", input
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/switch"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }
}
