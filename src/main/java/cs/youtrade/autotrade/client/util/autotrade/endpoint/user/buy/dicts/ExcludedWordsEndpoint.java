package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.dicts;

import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtWordsEndpoint;
import org.springframework.stereotype.Component;

@Component
public class ExcludedWordsEndpoint extends AbstractAtWordsEndpoint {
    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/buy/words/excluded";
    }
}
