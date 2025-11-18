package cs.youtrade.autotrade.client.util.autotrade.endpoint.parent;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.WordDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.buy.FcdWordsAddDto;

import java.util.List;
import java.util.Map;

public abstract class AbstractAtWordsEndpoint extends AbstractAtEndpoint{
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
                new TypeToken<>() {
                }
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
                new TypeToken<>() {
                }
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
                HttpMethod.DELETE,
                createEndpoint(),
                getHeaders(),
                params,
                req,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<Integer>> deleteAllWords(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.DELETE,
                createEndpoint("/all"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }
}
