package cs.youtrade.autotrade.client.telegram.prototype;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInitializer {
    private final GeneralEndpoint endpoint;

    public UserData initUser(Long chatId) {
        var ans = endpoint.viewAccInfo(chatId);
        if (ans.getStatus() >= 300)
            return new UserData(chatId);

        var response = ans.getResponse();
        if (!response.isResult())
            return new UserData(chatId);

        return new UserData(chatId, response);
    }
}
