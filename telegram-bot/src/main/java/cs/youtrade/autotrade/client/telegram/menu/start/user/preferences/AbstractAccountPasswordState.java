package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;

public abstract class AbstractAccountPasswordState extends YTPTerminalTextMenuState {
    protected final AccountPasswordRegistry registry;

    protected AbstractAccountPasswordState(UserTextMessageSender sender, AccountPasswordRegistry registry) {
        super(sender);
        this.registry = registry;
    }

    protected String summary(UserData user) {
        var data = registry.getOrCreate(user, AccountPasswordData::new);
        String currentPassword = data.getChanging() ? String.format("""
                • Текущий пароль: %s
                """, data.isCurrentProvided() ? "✅ Получен" : "—") : "";
        return String.format("""
                %s <i>Защитный пароль</i>

                %s <b>Данные аккаунтов</b>
                <blockquote>• Действие: <b>%s</b>
                %s• Новый пароль: %s</blockquote>
                └ <i>Один пароль для всех аккаунтов. От 12 до 256 символов.</i>

                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.SECURE.getEmoji(),
                data.getChanging() ? "Смена пароля" : "Установка пароля", currentPassword,
                data.isSaved() ? "✅ Сохранён" : "—");
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PREFERENCES;
    }
}
