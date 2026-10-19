package cs.youtrade.autotrade.client.telegram.menu.start.ref.connect.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.connect.RefConnectRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class RefConnectProceedState extends YTPTerminalTextMenuState {
    private final RefConnectRegistry registry;
    private final RefEndpoint endpoint;

    public RefConnectProceedState(
            UserTextMessageSender sender,
            RefConnectRegistry registry,
            RefEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_CONNECT_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "referral.RefInvitation");
        var data = registry.remove(user);
        var ans = endpoint.refConnect(user.getChatId(), data.getRef());
        if (ans.getStatus() >= 300)
            return null;

        var fcd = ans.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var ref = fcd.getData();
        return String.format("""
                        %s %s
                        
                        %s %s: <b>$%s</b>
                        """,
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.format("code-b", Map.of("value1", String.format("<code>%s</code>", ref.getThisRef()), "value2", String.format("<b>%s</b>", translations.get("activated-successfully")), "emphasis", String.format("<b>%s</b>", translations.get("code-b.emphasis")))),
                        DynamicEmoji.MONEY.getEmoji(),
                        translations.get("you-received"),
                        ref.getRefReward().toPlainString());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.REF;
    }
}
