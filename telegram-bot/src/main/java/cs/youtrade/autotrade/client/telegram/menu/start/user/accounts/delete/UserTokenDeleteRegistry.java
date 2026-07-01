package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.state.AbstractStateRegistry;
import org.springframework.stereotype.Service;

@Service
public class UserTokenDeleteRegistry extends AbstractStateRegistry<UserData, UserTokenDeleteData> {
}
