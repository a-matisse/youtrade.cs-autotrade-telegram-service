package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.abswitch.duplicate;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class SwitchDuplicateState extends YTPTerminalTextMenuState {
    private final BuyEndpoint buyEndpoint;

    public SwitchDuplicateState(
            UserTextMessageSender sender,
            BuyEndpoint buyEndpoint
    ) {
        super(sender);
        this.buyEndpoint = buyEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_SWITCH_DUPLICATE_MODE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = buyEndpoint.switchDuplicateMode(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return switch (fcd.getData()) {
            case NUMERIC -> String.format("%s <b>Выбран числовой режим дублирования</b>",
                    DynamicEmoji.NUMERIC.getEmoji());
            case PERCENTAGE -> String.format("%s <b>Выбран процентный режим дублирования</b>",
                    DynamicEmoji.GRAPH.getEmoji());
            case PERCENTAGE_PORTFOLIO -> String.format("%s <b>Выбран процентный (от банка) режим дублирования</b>",
                    DynamicEmoji.GRAPH.getEmoji());
        };
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }
}
