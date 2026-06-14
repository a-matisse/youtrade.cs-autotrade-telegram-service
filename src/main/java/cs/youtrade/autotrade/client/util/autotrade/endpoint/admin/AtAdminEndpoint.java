package cs.youtrade.autotrade.client.util.autotrade.endpoint.admin;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.admin.FcdAdminDeleteDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.admin.FcdAdminGiveBalanceDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.admin.FcdAdminUserDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.admin.FcdAdminUserRequestDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class AtAdminEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdAdminDeleteDto> deleteSub(
            Long chatId,
            Long tdId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "tdId", tdId.toString()
        );
        return client.fetchFromApi(HttpMethod.DELETE, createEndpoint("/sub"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdAdminDeleteDto>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<List<FcdAdminUserDto>>> getUsers(
    ) {
        return client.fetchFromApi(HttpMethod.GET, createEndpoint("/users"))
                .headers(getHeaders())
                .type(new TypeToken<FcdDefaultDto<List<FcdAdminUserDto>>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<List<FcdAdminUserRequestDto>>> getRequests(
    ) {
        return client.fetchFromApi(HttpMethod.GET, createEndpoint("/users/requests"))
                .headers(getHeaders())
                .type(new TypeToken<FcdDefaultDto<List<FcdAdminUserRequestDto>>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdAdminGiveBalanceDto> giveBalance(
            Long tdId,
            BigDecimal amount
    ) {
        Map<String, String> params = Map.of(
                "tdId", tdId.toString(),
                "amount", amount.toPlainString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/balance"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdAdminGiveBalanceDto>() {
                }.getType()).build().fetch();
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/admin";
    }
}
