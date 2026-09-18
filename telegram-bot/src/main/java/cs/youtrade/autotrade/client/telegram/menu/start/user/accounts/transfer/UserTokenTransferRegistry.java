package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.transfer;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdAccountsTransferInput;
import cs.youtrade.telegram.buttons.state.AbstractStateRegistry;
import org.springframework.stereotype.Service;

@Service
public class UserTokenTransferRegistry extends AbstractStateRegistry<UserData, FcdAccountsTransferInput> {
}
