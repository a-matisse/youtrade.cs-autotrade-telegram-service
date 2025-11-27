package cs.youtrade.autotrade.client.telegram.menu.main.params.token.rename;

import cs.youtrade.autotrade.client.telegram.menu.main.params.rename.UserRenameData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.state.AbstractStateRegistry;
import org.springframework.stereotype.Service;

@Service
public class UserTokenRenameRegistry extends AbstractStateRegistry<UserData, UserRenameData> {
}
