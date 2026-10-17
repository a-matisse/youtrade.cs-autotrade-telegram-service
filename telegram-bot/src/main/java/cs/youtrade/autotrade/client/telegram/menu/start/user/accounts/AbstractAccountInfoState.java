package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;

public abstract class AbstractAccountInfoState extends YTPTerminalTextMenuState {
    protected final AccountInfoRegistry registry;

    protected AbstractAccountInfoState(UserTextMessageSender sender, AccountInfoRegistry registry) {
        super(sender);
        this.registry = registry;
    }

    protected String summary(UserData user) {
        var data = registry.getOrCreate(user, AccountInfoData::new);
        return String.format("""
                %s <i>Данные аккаунта</i>

                %s <b>Аккаунт</b>
                <blockquote>• ID: %s
                • Защитный пароль: %s</blockquote>

                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.STEAM.getEmoji(),
                data.getTokenId() == null ? "—" : "<code>" + data.getTokenId() + "</code>",
                data.isPasswordVerified() ? "✅ Проверен" : "—");
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS_MORE;
    }
}
