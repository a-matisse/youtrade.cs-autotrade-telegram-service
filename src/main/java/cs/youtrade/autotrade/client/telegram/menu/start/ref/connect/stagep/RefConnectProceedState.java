package cs.youtrade.autotrade.client.telegram.menu.start.ref.connect.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.connect.RefConnectRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class RefConnectProceedState extends AbstractTerminalTextMenuState {
    private final RefConnectRegistry registry;
    private final RefEndpoint endpoint;

    public RefConnectProceedState(
            UserTextMessageSender sender,
            RefConnectRegistry registry,
            RefEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_CONNECT_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        var ans = endpoint.refConnect(user.getChatId(), data.getRef());
        if (ans.getStatus() >= 300)
            return null;

        var fcd = ans.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var ref = fcd.getData();
        return String.format("""
                        ✅ Код (%s) успешно активирован!
                        
                        💸 Вам начислено: $%s
                        """,
                ref.getThisRef(),
                ref.getRefReward().toPlainString()
        );
    }

    @Override
    public UserMenu retState() {
        return UserMenu.REF;
    }
}
