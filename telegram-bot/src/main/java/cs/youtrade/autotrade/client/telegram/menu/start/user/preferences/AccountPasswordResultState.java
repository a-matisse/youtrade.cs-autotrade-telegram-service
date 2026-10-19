package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AccountPasswordResultState extends AbstractAccountPasswordState {
    public AccountPasswordResultState(UserTextMessageSender sender, AccountPasswordRegistry registry, BotTexts texts) {
        super(sender, registry, texts);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.PREFERENCES_ACCOUNT_PASSWORD_RESULT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var text = texts.forUser(user, "preferences.AccountPassword");
        var data = registry.getOrCreate(user, AccountPasswordData::new);
        String result = switch (data.getResponseStatus() == null ? 0 : data.getResponseStatus()) {
            case 204 -> String.format("""
                    %s <b>%s</b>
                    """, DynamicEmoji.SUCCESS.getEmoji(), text.get(data.getChanging() ? "password-changed" : "password-set"));
            case 403 -> text.get("incorrect-current-password");
            case 409 -> text.get("password-state-changed");
            case 429 -> text.get("too-many-attempts");
            default -> text.get("save-failed");
        };
        return String.format("""
                %s%s
                """, summary(user), result);
    }
}
