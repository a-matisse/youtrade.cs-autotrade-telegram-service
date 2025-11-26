package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.follow;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.state.AbstractStateRegistry;
import org.springframework.stereotype.Service;

@Service
public class UserFollowRegistry extends AbstractStateRegistry<UserData, UserFollowData> {
}
