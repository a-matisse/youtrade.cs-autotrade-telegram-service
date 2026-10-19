package cs.youtrade.autotrade.client.telegram.menu.start.ref.create;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class RefCreateState extends YTPTerminalTextMenuState {
    private final RefEndpoint endpoint;

    public RefCreateState(
            UserTextMessageSender sender,
            RefEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_CREATE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "referral.RefInvitation");
        var ans = endpoint.refCreate(user.getChatId());
        if (ans.getStatus() >= 300)
            return null;

        var fcd = ans.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var ref = fcd.getData();
        return String.format("%s %s: <code>%s</code>",
                DynamicEmoji.SUCCESS.getEmoji(),
                translations.get("referral-code-created"),
                ref.getThisRef());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.REF;
    }
}
