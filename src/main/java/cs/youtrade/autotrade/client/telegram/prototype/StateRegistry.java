package cs.youtrade.autotrade.client.telegram.prototype;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.def.DefStateInt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StateRegistry {
    private final Map<UserMenu, DefStateInt<Long, UserMenu>> registry = new ConcurrentHashMap<>();

    public StateRegistry(List<DefStateInt<Long, UserMenu>> commands) {
        for (DefStateInt<Long, UserMenu> c : commands)
            registry.put(c.supportedState(), c);
    }

    public DefStateInt<Long, UserMenu> get(UserMenu state) {
        return registry.get(state);
    }
}
