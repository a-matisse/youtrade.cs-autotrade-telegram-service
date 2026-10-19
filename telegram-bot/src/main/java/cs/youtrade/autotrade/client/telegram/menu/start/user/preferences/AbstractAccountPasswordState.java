package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;

import java.util.Map;
import java.util.function.Function;

public abstract class AbstractAccountPasswordState extends YTPTerminalTextMenuState {
    protected final AccountPasswordRegistry registry;
    protected final BotTexts texts;

    protected AbstractAccountPasswordState(UserTextMessageSender sender, AccountPasswordRegistry registry,
                                           BotTexts texts) {
        super(sender, texts);
        this.registry = registry;
        this.texts = texts;
    }

    protected String summary(UserData user) {
        var text = texts.forUser(user, "preferences.AccountPassword");
        var data = registry.getOrCreate(user, AccountPasswordData::new);
        String currentPassword = data.getChanging() ? String.format("""
                • %s: %s
                """, text.get("current-password"), data.isCurrentProvided() ? "✅ " + text.get("received") : "—") : "";
        return String.format("""
                %s <i>%s</i>

                %s <b>%s</b>
                <blockquote>• %s: <b>%s</b>
                %s• %s: %s</blockquote>
                └ <i>%s</i>

                """, DynamicEmoji.YOUTRADE.getEmoji(), text.get("title"),
                DynamicEmoji.SECURE.getEmoji(), text.get("account-details"), text.get("action"),
                text.get(data.getChanging() ? "change-password" : "set-password"), currentPassword,
                text.get("new-password"), data.isSaved() ? "✅ " + text.get("saved") : "—", text.get("requirements"));
    }

    @Override
    public Map<TerminalMenu, Function<UserData, String>> getTextFunctions(UserData user) {
        return Map.of(TerminalMenu.RETURN, u -> TerminalMenu.RETURN.getButtonName(texts.forUser(u,
                "common.Interface").get("button.RETURN")));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PREFERENCES;
    }
}
