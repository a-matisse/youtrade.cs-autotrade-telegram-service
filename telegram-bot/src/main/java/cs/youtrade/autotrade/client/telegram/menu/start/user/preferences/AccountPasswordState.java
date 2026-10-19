package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AccountPasswordState extends AbstractAccountPasswordState {
    public AccountPasswordState(UserTextMessageSender sender, AccountPasswordRegistry registry, BotTexts texts) {
        super(sender, registry, texts);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PREFERENCES_ACCOUNT_PASSWORD;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var text = texts.forUser(user, "preferences.AccountPassword");
        return String.format("""
                %s%s <b>%s</b>
                """, summary(user), DynamicEmoji.WRITE.getEmoji(), text.get("send-current-password"));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var text = texts.forUser(user, "preferences.AccountPassword");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, text.get("send-password-as-text"));
            return supportedState();
        }
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        String value = update.getMessage().getText();
        if (value.isEmpty()) {
            sender.sendTextMes(bot, user, text.get("current-password-empty"));
            return supportedState();
        }
        var data = registry.getOrCreate(user, AccountPasswordData::new);
        data.setCurrentPassword(value);
        data.setCurrentProvided(true);
        return UserMenu.PREFERENCES_ACCOUNT_PASSWORD_NEW;
    }
}
