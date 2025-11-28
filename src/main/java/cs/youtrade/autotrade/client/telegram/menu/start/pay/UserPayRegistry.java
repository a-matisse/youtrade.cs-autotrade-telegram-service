package cs.youtrade.autotrade.client.telegram.menu.start.pay;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.state.AbstractStateRegistry;
import org.springframework.stereotype.Service;

@Service
public class UserPayRegistry extends AbstractStateRegistry<UserData, UserPayData> {
}
