package cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.prototype;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.ParamsCreateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractCreateState extends AbstractTextMenuState<MarketType> {
    protected final ParamsCreateRegistry registry;

    public AbstractCreateState(
            UserTextMessageSender sender,
            ParamsCreateRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public Map<MarketType, Predicate<UserData>> getVisibilityPredicates(UserData user) {
        return Arrays
                .stream(MarketType.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        type -> data -> visibilityCondition(type)
                ));
    }

    public abstract boolean visibilityCondition(MarketType type);
}
