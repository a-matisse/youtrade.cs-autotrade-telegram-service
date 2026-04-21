package cs.youtrade.autotrade.client.telegram.menu.start.user.token.rename.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.rename.UserTokenRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ChangeNameOption;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TokenRenameProceedState extends YTPTerminalTextMenuState {
    private static final ChangeNameOption opt = ChangeNameOption.TOKEN;

    private final UserTokenRenameRegistry registry;
    private final GeneralEndpoint endpoint;

    public TokenRenameProceedState(
            UserTextMessageSender sender,
            UserTokenRenameRegistry registry,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_RENAME_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.changeName(user.getChatId(), opt.name(), data.getId(), data.getValue());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("%s <b>Новое имя аккаунта установлено</b>",
                DynamicEmoji.SUCCESS.getEmoji());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.TOKEN;
    }
}
