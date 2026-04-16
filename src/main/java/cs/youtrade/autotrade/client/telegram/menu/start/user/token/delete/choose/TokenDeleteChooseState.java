package cs.youtrade.autotrade.client.telegram.menu.start.user.token.delete.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.delete.TokenDeleteOption;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.delete.UserTokenDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TokenDeleteChooseState extends YTPTextMenuState<TokenDeleteOption> {
    private final UserTokenDeleteRegistry registry;

    public TokenDeleteChooseState(
            UserTextMessageSender sender,
            UserTokenDeleteRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_REMOVE_STAGE_CHOOSE;
    }

    @Override
    public TokenDeleteOption getOption(String optionStr) {
        return TokenDeleteOption.valueOf(optionStr);
    }

    @Override
    public TokenDeleteOption[] getOptions(UserData userData) {
        return TokenDeleteOption.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TokenDeleteOption t) {
        var data = registry.getOrCreate(user, UserTokenDeleteData::new);
        data.setOpt(t);
        return switch (t) {
            case SINGLE -> UserMenu.TOKEN_REMOVE_STAGE_1;
            case ALL -> UserMenu.TOKEN_REMOVE_STAGE_P;
            case RETURN -> UserMenu.TOKEN;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return """
                🗑️ <b>Режим удаления</b>
                ━━━━━━━━━━
                <blockquote>• <b>Одиночный режим</b> — <b>выбираете ID аккаунта</b> для удаления
                • <b>Массовый режим</b> — <b>удаляете все аккаунты</b>, привязанные к текущим параметрам</blockquote>
                
                Выберите подходящий вариант...
                """;
    }
}
