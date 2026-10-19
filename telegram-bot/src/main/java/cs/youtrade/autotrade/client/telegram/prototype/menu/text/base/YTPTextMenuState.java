package cs.youtrade.autotrade.client.telegram.prototype.menu.text.base;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.telegram.buttons.menu.text.AbstractTextMenuState;
import cs.youtrade.telegram.buttons.sender.text.BaseTextMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.function.Function;

public abstract class YTPTextMenuState<MENU extends IMenuEnum> extends AbstractTextMenuState<UserData, MENU, UserMenu> {
    protected final BotTexts botTexts;
    public YTPTextMenuState(
            BaseTextMessageSender<UserData> sender,
            BotTexts botTexts
    ) {
        super(sender);
        this.botTexts = botTexts;
    }

    @Override
    public Map<MENU, Function<UserData, String>> getTextFunctions(UserData user) {
        return botTexts.buttons(getOptions(user));
    }

    @Override
    public void sendDefErrMes(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "common.Interface");
        sender.sendTextMes(bot, userData, ErrorMessageGenerator.getErrorText(translations));
    }
}
