package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.ChangeNameOption;
import cs.youtrade.autotrade.client.util.autotrade.TdpField;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralNewestDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdTokenGetSingleDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GeneralEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdGeneralNewestDto> getDataLastHrs(
            Long chatId,
            Integer hrs
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "hrs", hrs.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint("/newest"),
                getHeaders(),
                params,
                new TypeToken<FcdGeneralNewestDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<ChangeNameOption>> changeName(
            Long chatId,
            String optStr,
            Long id,
            String name
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "optStr", optStr,
                "id", id.toString(),
                "name", name
        );
        return client.fetchFromApi(
                HttpMethod.PUT,
                createEndpoint("/name"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<ChangeNameOption>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<TdpField>> changeField(
            Long chatId,
            String fName,
            String value
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "fName", fName,
                "value", value
        );
        return client.fetchFromApi(
                HttpMethod.PUT,
                createEndpoint("/field"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<TdpField>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdGeneralAccInfoDto> initUser(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/init"),
                getHeaders(),
                params,
                new TypeToken<FcdGeneralAccInfoDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdGeneralAccInfoDto> viewAccInfo(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint("/info"),
                getHeaders(),
                params,
                new TypeToken<FcdGeneralAccInfoDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<List<FcdTokenGetSingleDto>>> getTokens(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint("/token/all"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<List<FcdTokenGetSingleDto>>>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/general";
    }
}
