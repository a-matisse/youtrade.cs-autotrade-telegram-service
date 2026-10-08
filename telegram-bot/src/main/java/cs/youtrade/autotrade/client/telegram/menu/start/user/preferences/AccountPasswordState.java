package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.properties.PropertiesEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountPasswordState extends YTPTextMenuState<AccountPasswordMenu> {
    private final PropertiesEndpoint endpoint;
    private final Map<Long, Data> registry = new ConcurrentHashMap<>();

    public AccountPasswordState(UserTextMessageSender sender, PropertiesEndpoint endpoint) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.PREFERENCES_ACCOUNT_PASSWORD; }

    @Override
    public AccountPasswordMenu getOption(String optionStr) { return AccountPasswordMenu.valueOf(optionStr); }

    @Override
    public AccountPasswordMenu[] getOptions(UserData user) { return AccountPasswordMenu.values(); }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, AccountPasswordMenu option) {
        registry.remove(user.getChatId());
        return UserMenu.PREFERENCES;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        Data data = data(user);
        StringBuilder text = new StringBuilder(String.format("""
                        %s <i>Защитный пароль</i>

                        %s <b>Данные аккаунтов</b>
                        <blockquote>• Действие: <b>%s</b>
                        """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.SECURE.getEmoji(),
                data.changing ? "Смена пароля" : "Установка пароля"));
        if (data.changing)
            text.append("• Текущий пароль: ").append(data.currentProvided ? "✅ Получен" : "—").append('\n');
        text.append("• Новый пароль: ").append(data.saved ? "✅ Сохранён" : "—")
                .append("</blockquote>\n└ <i>Один пароль для всех аккаунтов. От 12 до 256 символов.</i>\n\n");
        text.append(switch (data.stage) {
            case CURRENT -> DynamicEmoji.WRITE.getEmoji() + " <b>Отправьте текущий пароль</b>";
            case NEW -> DynamicEmoji.WRITE.getEmoji() + " <b>Отправьте новый пароль</b>";
            case DONE -> data.result;
        });
        return text.toString();
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте пароль текстовым сообщением.");
            return supportedState();
        }
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        Data data = data(user);
        if (data.stage == Stage.DONE) return supportedState();
        String value = update.getMessage().getText();
        if (data.stage == Stage.CURRENT) {
            if (value.isEmpty()) sender.sendTextMes(bot, user, "Текущий пароль не может быть пустым.");
            else {
                data.currentPassword = value;
                data.currentProvided = true;
                data.stage = Stage.NEW;
            }
            return supportedState();
        }
        if (value.length() < 12 || value.length() > 256) {
            sender.sendTextMes(bot, user, "Новый пароль должен содержать от 12 до 256 символов.");
            return supportedState();
        }
        try {
            var answer = data.changing
                    ? endpoint.changeAccountExportPassword(user.getChatId(), data.currentPassword, value)
                    : endpoint.setAccountExportPassword(user.getChatId(), value);
            data.result = switch (answer.getStatus()) {
                case 204 -> DynamicEmoji.SUCCESS.getEmoji() + " <b>Пароль "
                        + (data.changing ? "изменён" : "установлен") + "</b>";
                case 403 -> "Неверный текущий пароль. Попробуйте снова позже.";
                case 409 -> "Состояние пароля изменилось. Вернитесь в настройки и откройте меню снова.";
                case 429 -> "Слишком много попыток. Повторите через 15 минут.";
                default -> "Не удалось сохранить пароль. Попробуйте позже.";
            };
            if (answer.getStatus() == 204) {
                data.saved = true;
                user.setAccountExportPasswordSet(true);
            }
        } catch (RuntimeException error) {
            data.result = "Не удалось сохранить пароль. Попробуйте позже.";
        } finally {
            data.currentPassword = null;
            data.stage = Stage.DONE;
        }
        return supportedState();
    }

    private Data data(UserData user) {
        return registry.computeIfAbsent(user.getChatId(), _ -> new Data(user.isAccountExportPasswordSet()));
    }

    private enum Stage { CURRENT, NEW, DONE }

    private static class Data {
        private final boolean changing;
        private volatile Stage stage;
        private String currentPassword;
        private boolean currentProvided;
        private boolean saved;
        private String result;

        private Data(boolean changing) {
            this.changing = changing;
            this.stage = changing ? Stage.CURRENT : Stage.NEW;
        }
    }
}
