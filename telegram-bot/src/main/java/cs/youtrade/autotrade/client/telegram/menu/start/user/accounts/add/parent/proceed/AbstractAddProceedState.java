package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.proceed;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;

public abstract class AbstractAddProceedState extends YTPTerminalTextMenuState {
    public AbstractAddProceedState(
            UserTextMessageSender sender,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }
}
