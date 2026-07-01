package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.unfollow;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.state.AbstractStateRegistry;
import org.springframework.stereotype.Service;

@Service
public class UserUnfollowRegistry extends AbstractStateRegistry<UserData, UserUnfollowData> {
}
