package cs.youtrade.autotrade.client.telegram.prototype.menu;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public interface MenuStateInt<
        USER extends AbstractUserData,
        MENU_TYPE extends IMenuEnum,
        MENU extends Enum<MENU>
        > {
    List<InlineKeyboardRow> buildKeyboard(UserData user);

    InlineKeyboardMarkup buildMarkup(UserData user);

    MENU_TYPE getOption(String optionStr);

    MENU_TYPE[] getOptions();

    MENU executeCallback(TelegramClient bot, Update update, USER user, MENU_TYPE t);

    String getHeaderText(TelegramClient bot, USER user);
}
