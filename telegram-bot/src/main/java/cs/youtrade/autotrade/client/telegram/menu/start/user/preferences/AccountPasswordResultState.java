package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AccountPasswordResultState extends AbstractAccountPasswordState {
    public AccountPasswordResultState(UserTextMessageSender sender, AccountPasswordRegistry registry) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.PREFERENCES_ACCOUNT_PASSWORD_RESULT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.getOrCreate(user, AccountPasswordData::new);
        String result = switch (data.getResponseStatus() == null ? 0 : data.getResponseStatus()) {
            case 204 -> String.format("""
                    %s <b>Пароль %s</b>
                    """, DynamicEmoji.SUCCESS.getEmoji(), data.getChanging() ? "изменён" : "установлен");
            case 403 -> "Неверный текущий пароль. Попробуйте снова позже.";
            case 409 -> "Состояние пароля изменилось. Вернитесь в настройки и откройте меню снова.";
            case 429 -> "Слишком много попыток. Повторите через 15 минут.";
            default -> "Не удалось сохранить пароль. Попробуйте позже.";
        };
        return String.format("""
                %s%s
                """, summary(user), result);
    }
}
