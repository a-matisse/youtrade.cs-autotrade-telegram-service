package cs.youtrade.autotrade.client.telegram.menu.main.mcreate.parent;

import cs.youtrade.autotrade.client.telegram.menu.main.mcreate.ParamsCreateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractDefState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.MessageSenderInt;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.stream.Collectors;

import static cs.youtrade.autotrade.client.util.autotrade.FcdStringUtils.findClosest;

public abstract class AbstractCreateState extends AbstractDefState<UserData, SendMessage> {
    protected final ParamsCreateRegistry registry;

    public AbstractCreateState(
            MessageSenderInt<UserData, SendMessage> sender,
            ParamsCreateRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public SendMessage buildMessage(UserData e) {
        var builder = SendMessage.builder();
        builder.chatId(e.getChatId());
        builder.text(getMessage());

        return builder.build();
    }

    protected MarketType findClosestMarketType(String input) {
        return findClosest(MarketType.values(), input);
    }

    protected String getMarketNames() {
        return Arrays.stream(MarketType.values())
                .map(MarketType::name)
                .collect(Collectors.joining(", "));
    }

    protected abstract String getMessage();
}
