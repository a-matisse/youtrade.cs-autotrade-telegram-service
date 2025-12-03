package cs.youtrade.autotrade.client.telegram.messaging;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.StateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class TelegramUpdReceiverService {
    private final TelegramClient bot;
    private final BotCommandProvider provider;
    private final TelegramSendMessageService sender;
    private final StateRegistry stateRegistry;

    private final ConcurrentHashMap<UserData, UserStateData> awaiting = new ConcurrentHashMap<>();

    @Autowired
    public TelegramUpdReceiverService(
            @Value("${tg.token.main}") String botToken,
            BotCommandProvider provider,
            TelegramSendMessageService sender,
            StateRegistry stateRegistry
    ) {
        this.bot = new OkHttpTelegramClient(botToken);
        this.provider = provider;
        this.sender = sender;
        this.stateRegistry = stateRegistry;
    }

    public void consume(Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery())
            return;

        // 1. Поиск пользователя в системе
        Long chatId = update.hasMessage()
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId();

        // 2. Выполнение запроса
        try {
            UserData user = new UserData(chatId);
            proceedTask(user, update);
        } catch (TelegramApiException e) {
            log.error("Couldn't proceed the update because of an error: {}", e.getMessage());
        }
    }

    private void proceedTask(UserData user, Update update) throws TelegramApiException {
        UserStateData stateData = getState(user);
        if (stateData == null)
            return;

        boolean isCmd = false;
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            UserMenu newMenu = provider.getCommandByCmd(text);
            if (newMenu != null) {
                stateData.setMenuState(newMenu);
                isCmd = true;
            }
        }

        // 2. Выполнение команды
        UserMenu state = stateData.getMenuState();
        UserMenu newState;
        try {
            newState = stateRegistry.get(state).execute(bot, update, user);
        } catch (Exception e) {
            log.error("Couldn't proceed the update because of an error: {}", e.getMessage(), e);
            newState = UserMenu.START;
        }

        // 3. Сохранение нового состояния
        stateData.setMenuState(newState);
        awaiting.put(user, stateData);

        // 4. Вывод сообщения нового состояния, если это не команда
        if (!isCmd && state != newState)
            stateRegistry.get(newState).executeOnState(bot, update, user);

        // 5. Удаление прошлого меню, чтобы не флудить сообщениями с кнопками (избыточно для пользователя)
        if (update.hasCallbackQuery())
            sender.deleteCallback(bot, user.getChatId(), update);
    }

    private void setCommandsForUser(UserData user) throws TelegramApiException {
        SetMyCommands setMyCommands = SetMyCommands
                .builder()
                .commands(provider.getBotCommands())
                .scope(new BotCommandScopeChat(user.getChatId().toString()))
                .build();

        bot.execute(setMyCommands);
    }

    private UserStateData getState(UserData user) {
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
}
