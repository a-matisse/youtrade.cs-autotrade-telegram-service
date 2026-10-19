package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.properties.PropertiesEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AccountPasswordNewState extends AbstractAccountPasswordState {
    private final PropertiesEndpoint endpoint;

    public AccountPasswordNewState(UserTextMessageSender sender, AccountPasswordRegistry registry,
                                   PropertiesEndpoint endpoint, BotTexts texts) {
        super(sender, registry, texts);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.PREFERENCES_ACCOUNT_PASSWORD_NEW; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var text = texts.forUser(user, "preferences.AccountPassword");
        return String.format("""
                %s%s <b>%s</b>
                """, summary(user), DynamicEmoji.WRITE.getEmoji(), text.get("send-new-password"));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var text = texts.forUser(user, "preferences.AccountPassword");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, text.get("send-password-as-text"));
            return supportedState();
        }
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        var data = registry.getOrCreate(user, AccountPasswordData::new);
        String value = update.getMessage().getText();
        if (value.length() < 12 || value.length() > 256) {
            sender.sendTextMes(bot, user, text.get("new-password-length"));
            return supportedState();
        }
        synchronized (data) {
            if (data.getResponseStatus() != null) return UserMenu.PREFERENCES_ACCOUNT_PASSWORD_RESULT;
            try {
                var answer = data.getChanging()
                        ? endpoint.changeAccountExportPassword(user.getChatId(), data.getCurrentPassword(), value)
                        : endpoint.setAccountExportPassword(user.getChatId(), value);
                data.setResponseStatus(answer.getStatus());
                if (answer.getStatus() == 204) {
                    data.setSaved(true);
                    user.setAccountExportPasswordSet(true);
                }
            } catch (RuntimeException error) {
                data.setResponseStatus(0);
            } finally {
                data.setCurrentPassword(null);
            }
            return UserMenu.PREFERENCES_ACCOUNT_PASSWORD_RESULT;
        }
    }
}
