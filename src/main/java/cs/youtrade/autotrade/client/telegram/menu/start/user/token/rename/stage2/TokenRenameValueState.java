package cs.youtrade.autotrade.client.telegram.menu.start.user.token.rename.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserRenameData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.rename.UserTokenRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TokenRenameValueState extends YTPTextState {
    private final UserTokenRenameRegistry registry;

    public TokenRenameValueState(
            UserTextMessageSender sender,
            UserTokenRenameRegistry registry
    ) {

        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Теперь введите новое имя для аккаунта (<tg-spoiler>до 100 символов</tg-spoiler>)...</b>
                        
                        <blockquote><i>Примеры имён</i>
                        
                        • <b>token_main</b>
                        • <b>главный аккаунт</b>
                        • <b>account 37</b></blockquote>""",
                DynamicEmoji.WRITE.getEmoji()
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_RENAME_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.TOKEN;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setValue(value);
        return UserMenu.TOKEN_RENAME_STAGE_P;
    }
}
