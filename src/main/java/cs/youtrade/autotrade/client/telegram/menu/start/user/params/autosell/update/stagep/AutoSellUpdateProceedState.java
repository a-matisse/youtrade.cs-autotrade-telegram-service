package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.UserAutoSellUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoSellUpdateProceedState extends YTPTerminalTextMenuState {
    private final UserAutoSellUpdateRegistry registry;
    private final GeneralEndpoint endpoint;

    public AutoSellUpdateProceedState(
            UserTextMessageSender sender,
            UserAutoSellUpdateRegistry registry,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.changeField(user.getChatId(), data.getField().getFName(), data.getValue());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("%s Поле %s успешно обновлено",
                DynamicEmoji.SUCCESS.getEmoji(), data.getField().getFName());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOSELL;
    }
}
