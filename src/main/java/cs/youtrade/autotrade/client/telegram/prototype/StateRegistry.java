package cs.youtrade.autotrade.client.telegram.prototype;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.BotCommandProvider;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.DefStateInt;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractNotificationMenuState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StateRegistry {
    private final TelegramClient bot;
    private final BotCommandProvider provider;
    private final TelegramSendMessageService sender;

    private final Map<UserMenu, DefStateInt<UserData, UserMenu, ?>> menuRegistry = new ConcurrentHashMap<>();
    private final Map<UserMenu, AbstractNotificationMenuState<?, ?>> notificationRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UserData, UserStateData> awaiting = new ConcurrentHashMap<>();

    public StateRegistry(
            TelegramClient bot,
            BotCommandProvider provider,
            TelegramSendMessageService sender,
            List<DefStateInt<UserData, UserMenu, ?>> menuList,
            List<AbstractNotificationMenuState<?, ?>> notificationList
    ) {
        this.bot = bot;
        this.provider = provider;
        this.sender = sender;

        for (var c : menuList)
            menuRegistry.put(c.supportedState(), c);
        for (var c : notificationList)
            notificationRegistry.put(c.supportedState(), c);
    }

    public DefStateInt<UserData, UserMenu, ?> getMenu(UserMenu state) {
        return menuRegistry.get(state);
    }

    public AbstractNotificationMenuState<?, ?> getNotification(UserMenu state) {
        return notificationRegistry.get(state);
    }

    public void put(UserData user, UserStateData state) {
        awaiting.put(user, state);
    }

    public UserStateData getState(UserData user) {
        return awaiting.computeIfAbsent(user, id -> {
            try {
                setCommandsForUser(user);
                return new UserStateData(UserMenu.START);
            } catch (TelegramApiException e) {
                sender.sendMessage(bot, user.getChatId(), "#-1: Не удалось сменить команды пользователя");
                return null;
            }
        });
    }

    private void setCommandsForUser(UserData user) throws TelegramApiException {
        SetMyCommands setMyCommands = SetMyCommands
                .builder()
                .commands(provider.getBotCommands())
                .scope(new BotCommandScopeChat(user.getChatId().toString()))
                .build();

        bot.execute(setMyCommands);
    }
}
