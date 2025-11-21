package cs.youtrade.autotrade.client.telegram.prototype.menu;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public interface MenuStateInt<USER, MENU_TYPE extends MenuEnumInterface, MENU extends Enum<MENU>> {
    List<InlineKeyboardRow> buildKeyboard();
    SendMessage buildMessage(USER user);
    MENU_TYPE getOption(String optionStr);
    MENU_TYPE[] getOptions();
    String getMenuHeader(USER user);
    MENU executeCallback(TelegramClient bot, Update update, USER user, MENU_TYPE t);
}
