package cs.youtrade.autotrade.client.telegram.menu.main.items;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.state.AbstractStateRegistry;
import org.springframework.stereotype.Service;

@Service
public class GetNewestItemsRegistry extends AbstractStateRegistry<UserData, GetNewestItemsData> {
}
