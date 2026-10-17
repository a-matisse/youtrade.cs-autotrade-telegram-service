package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.state.AbstractStateRegistry;
import org.springframework.stereotype.Component;

@Component
public class AccountInfoRegistry extends AbstractStateRegistry<UserData, AccountInfoData> {
}
