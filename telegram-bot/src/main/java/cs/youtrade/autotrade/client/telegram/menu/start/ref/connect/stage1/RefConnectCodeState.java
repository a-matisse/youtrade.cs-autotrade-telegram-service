package cs.youtrade.autotrade.client.telegram.menu.start.ref.connect.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.connect.RefConnectData;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.connect.RefConnectRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class RefConnectCodeState extends YTPTextState {
    private final RefConnectRegistry registry;

    public RefConnectCodeState(
            UserTextMessageSender sender,
            RefConnectRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "referral.RefInvitation");
        return String.format("%s %s",
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("enter-a-referral-code-to-activate-it"));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_CONNECT_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "referral.RefInvitation");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.SCORING;
        }

        String input = update.getMessage().getText();
        registry.setReferral(user, input);
        return UserMenu.REF_CONNECT_STAGE_P;
    }
}
