package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.notification.YTNotificationType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractNotificationMenuState<MENU_TYPE extends IMenuEnum, D>
        extends AbstractTextMenuState<MENU_TYPE> {
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

    @Override
    public void executeOnState(TelegramClient bot, UserData user, UserStateData lastState, Object data) {
        try {
            if (lastState != null && !lastState.getMenuState().isNotification())
                lastMenu.put(user, lastState.getMenuState());
            sender.sendMessage(bot, user, buildMessage(bot, user, data));
        } catch (IllegalArgumentException e) {
            sendDefErrMes(bot, user.getChatId());
        }
    }

    @Override
    public SendMessage buildMessage(TelegramClient bot, UserData user, Object data) {
        String ans = "";
        String header = getHeaderText(bot, user, (D) data);
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

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return "";
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
