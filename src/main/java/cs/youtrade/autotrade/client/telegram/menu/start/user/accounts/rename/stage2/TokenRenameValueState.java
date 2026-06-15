package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.rename.stage2;

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
            UserTokenRenameRegistry registry
    ) {

        super(sender);
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
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.ACCOUNTS;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(userData, UserRenameData::new);
        data.setValue(value);
        return UserMenu.ACCOUNTS_RENAME_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Теперь введите новое имя для аккаунта (<tg-spoiler>до 100 символов</tg-spoiler>)...</b>
                        
                        <blockquote><i>Примеры имён</i>
                        
                        • <b>token_main</b>
                        • <b>главный аккаунт</b>
                        • <b>account 37</b></blockquote>""",
                DynamicEmoji.WRITE.getEmoji()
        );
    }
}
