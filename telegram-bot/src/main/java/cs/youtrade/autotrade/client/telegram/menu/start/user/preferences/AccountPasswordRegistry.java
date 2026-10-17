package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.state.AbstractStateRegistry;
import org.springframework.stereotype.Component;

@Component
public class AccountPasswordRegistry extends AbstractStateRegistry<UserData, AccountPasswordData> {
}
