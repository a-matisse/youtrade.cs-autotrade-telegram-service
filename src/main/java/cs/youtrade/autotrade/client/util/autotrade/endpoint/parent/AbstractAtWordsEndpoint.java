package cs.youtrade.autotrade.client.util.autotrade.endpoint.parent;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.WordDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.buy.FcdWordsAddDto;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;

import java.util.List;
import java.util.Map;

public abstract class AbstractAtWordsEndpoint extends AbstractAtEndpoint{
    public RestAnswer<FcdDefaultDto<Long>> wordsCount(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint("/count"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<List<WordDto>>> wordsGet(
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
                new TypeToken<FcdDefaultDto<List<WordDto>>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdWordsAddDto> wordsAdd(
            Long chatId,
            List<String> req
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                params,
                req,
                new TypeToken<FcdWordsAddDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Integer>> deleteWords(
            Long chatId,
            List<Long> req
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/delete"),
                getHeaders(),
                params,
                req,
                new TypeToken<FcdDefaultDto<Integer>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Integer>> deleteAllWords(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/delete/all"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<Integer>>() {
                }.getType()
        );
    }
}
