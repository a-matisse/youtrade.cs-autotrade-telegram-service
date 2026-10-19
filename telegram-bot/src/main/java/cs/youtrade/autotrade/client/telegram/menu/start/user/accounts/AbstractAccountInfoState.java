package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;

public abstract class AbstractAccountInfoState extends YTPTerminalTextMenuState {
    protected final AccountInfoRegistry registry;

    protected AbstractAccountInfoState(UserTextMessageSender sender, AccountInfoRegistry registry, BotTexts botTexts) {
        super(sender, botTexts);
        this.registry = registry;
    }

    protected String summary(UserData user) {
        var translations = botTexts.forUser(user, "accounts.AccountInfo");
        var data = registry.getOrCreate(user, AccountInfoData::new);
        return String.format("""
                %s <i>%s</i>

                %s <b>%s</b>
                <blockquote>• ID: %s
                • %s: %s</blockquote>

                """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                translations.get("account-details"),
                DynamicEmoji.STEAM.getEmoji(),
                translations.get("account"),
                data.getTokenId() == null ? "—" : "<code>" + data.getTokenId() + "</code>",
                translations.get("security-password-2"),
                data.isPasswordVerified() ? String.format("✅ %s",
                        translations.get("verified")) : "—");
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS_MORE;
    }
}
