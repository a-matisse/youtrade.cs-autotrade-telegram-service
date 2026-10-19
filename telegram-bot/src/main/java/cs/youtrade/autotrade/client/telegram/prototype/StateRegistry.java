package cs.youtrade.autotrade.client.telegram.prototype;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.BotCommandProvider;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTNotificationMenu;
import cs.youtrade.telegram.buttons.def.DefStateInt;
import cs.youtrade.telegram.buttons.sender.BaseSendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StateRegistry {
    private final TelegramClient bot;
    private final BotCommandProvider provider;
    private final BaseSendMessageService sender;
    private final BotTexts texts;

    private final Map<UserMenu, DefStateInt<UserData, UserMenu, ?, ?>> menuRegistry = new ConcurrentHashMap<>();
    private final Map<UserMenu, YTNotificationMenu<?, ?>> notificationRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UserData, UserStateData> awaiting = new ConcurrentHashMap<>();

    public StateRegistry(
            TelegramClient bot,
            BotCommandProvider provider,
            BaseSendMessageService sender,
            List<DefStateInt<UserData, UserMenu, ?, ?>> menuList,
            List<YTNotificationMenu<?, ?>> notificationList,
            BotTexts texts
    ) {
        this.bot = bot;
        this.provider = provider;
        this.sender = sender;
        this.texts = texts;

        for (var c : menuList)
            menuRegistry.put(c.supportedState(), c);
        for (var c : notificationList)
            notificationRegistry.put(c.supportedState(), c);
    }

    public DefStateInt<UserData, UserMenu, ?, ?> getMenu(UserMenu state) {
        return menuRegistry.get(state);
    }

    public YTNotificationMenu<?, ?> getNotification(UserMenu state) {
        return notificationRegistry.get(state);
    }

    public void put(UserData user, UserStateData state) {
        awaiting.put(user, state);
    }

    public UserStateData getState(UserData user) {
        try {
            provider.updateCommands(bot, user);
            return awaiting.computeIfAbsent(user, id -> new UserStateData(UserMenu.START));
        } catch (TelegramApiException e) {
            sender.sendMessage(bot, user.getChatId(),
                    texts.forUser(user, "common.Interface").get("commands-update-failed"), null);
            return null;
        }
    }

}
