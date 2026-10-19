package cs.youtrade.autotrade.client.telegram.prototype.menu.text.base;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.def.AbstractTextState;
import cs.youtrade.telegram.buttons.sender.IMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class YTPTextState extends AbstractTextState<UserData, UserMenu> {
    protected final BotTexts botTexts;
    public YTPTextState(IMessageSender<UserData, SendMessage, EditMessageText> sender, BotTexts botTexts) {
        super(sender);
        this.botTexts = botTexts;
    }

    @Override
    public void sendDefErrMes(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "common.Interface");
        sender.sendTextMes(bot, userData, ErrorMessageGenerator.getErrorText(translations));
    }
}
