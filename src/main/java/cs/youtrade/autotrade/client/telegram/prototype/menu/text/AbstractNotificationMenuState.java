package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.notification.YTNotificationType;
import cs.youtrade.telegram.buttons.IMenuEnum;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractNotificationMenuState<MENU_TYPE extends IMenuEnum, D>
        extends YTPTextMenuState<MENU_TYPE> {
    private final Map<UserData, D> dataMap = new ConcurrentHashMap<>();
    private final Map<UserData, UserMenu> lastMenu = new ConcurrentHashMap<>();

    public AbstractNotificationMenuState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public UserMenu errorType(UserData userData) {
        return getReturnMenu(userData);
    }

    public void executeOnState(TelegramClient bot, UserData user, UserStateData lastState, Object data) {
        try {
            // Проверяем и определяем меню для возврата
            if (lastState != null && !lastState.getMenuState().isNotification())
                lastMenu.put(user, lastState.getMenuState());
            // Сохраняем данные
            dataMap.put(user, (D) data);
            executeOnState(bot, null, user);
        } catch (IllegalArgumentException e) {
            sendDefErrMes(bot, user);
        }
    }

    @Override
    public SendMessage buildMessage(TelegramClient bot, Update update, UserData userData) {
        D data = dataMap.get(userData);
        return data != null
                ? buildMessage(bot, userData, data)
                : null;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        D data = dataMap.get(userData);
        return data != null
                ? getHeaderText(bot, userData, data)
                : null;
    }

    public SendMessage buildMessage(TelegramClient bot, UserData user, D data) {
        String ans = "";
        String header = getHeaderText(bot, user, data);
        if (header != null)
            ans = header;
        if (ans.isEmpty())
            ans = "Не удалось обработать сообщение";

        return SendMessage
                .builder()
                .text(ans)
                .chatId(user.getChatId())
                .replyMarkup(buildMarkup(user))
                .parseMode(ParseMode.HTML)
                .build();
    }

    public UserMenu getReturnMenu(UserData userData) {
        var toReturn = lastMenu.get(userData);
        return toReturn == null
                ? UserMenu.START
                : toReturn;
    }

    public abstract String getHeaderText(TelegramClient bot, UserData user, D data);

    public abstract YTNotificationType getNotificationType();
}
