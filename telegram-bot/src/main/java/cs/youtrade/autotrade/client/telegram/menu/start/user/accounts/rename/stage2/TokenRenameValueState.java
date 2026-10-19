package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.rename.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserRenameData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.rename.UserTokenRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TokenRenameValueState extends YTPTerminalTextMenuState {
    private final UserTokenRenameRegistry registry;

    public TokenRenameValueState(
            UserTextMessageSender sender,
            UserTokenRenameRegistry registry,
            BotTexts botTexts
    ) {

        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_RENAME_STAGE_2;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.TokenRename");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.ACCOUNTS;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(userData, UserRenameData::new);
        data.setValue(value);
        return UserMenu.ACCOUNTS_RENAME_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.TokenRename");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote><i>%s</i>
                        
                        • <b>token_main</b>
                        • <b>%s</b>
                        • <b>account 37</b></blockquote>""",
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.format("now-enter-a-new-account-name", Map.of("value1", String.format("<tg-spoiler>%s</tg-spoiler>", translations.get("up-to-100-characters")))),
                        translations.get("example-names"),
                        translations.get("main-account"));
    }
}
