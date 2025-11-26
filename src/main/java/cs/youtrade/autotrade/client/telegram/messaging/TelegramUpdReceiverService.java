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
    private final String botToken;
    private final TelegramSendMessageService sender;
    private final StateRegistry stateRegistry;

    private final ConcurrentHashMap<UserData, UserStateData> awaiting = new ConcurrentHashMap<>();

    @Autowired
    public TelegramUpdReceiverService(
            @Value("${tg.token}") String botToken,
            TelegramSendMessageService sender,
            StateRegistry stateRegistry
    ) {
        this.bot = new OkHttpTelegramClient(botToken);
        this.botToken = botToken;
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
            procedeTask(user, update);
        } catch (TelegramApiException e) {
            log.error("Couldn't proceed the update because of an error: {}", e.getMessage());
        }
    }

    private void procedeTask(UserData user, Update update) throws TelegramApiException {
        UserStateData stateData = awaiting.computeIfAbsent(user, id ->
                new UserStateData(UserMenu.MAIN));

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                String mes = """
                        👋 Добро пожаловать в YouTradeSg!
                        
                        🔐 YouTradeSg - Безопасное управление токенами
                        
                        Храните токены и делитесь доступом с друзьями!
                         • Ваши данные под защитой
                         • Гостевой доступ по запросу
                         • Прозрачный контроль просмотров
                        
                        Для работы с ботом требуется авторизация:
                         • Если вы здесь впервые или давно не заходили - введите пароль для доступа к вашему аккаунту.
                         • Если уже авторизованы - используйте /menu для получения актуального меню.
                        
                        📝 Пароль потребуется только один раз за сессию.
                        """;
                sender.sendMessage(bot, user.getChatId(), mes);
                setCommandsForUser(user);
                return;
            }
        }

        // 0. Проверка команд
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.equals("/menu"))
                stateData.setMenuState(UserMenu.MAIN);
        }

        // 2. Выполнение команды
        UserMenu state = stateData.getMenuState();
        UserMenu newState = stateRegistry.get(state).execute(bot, update, user);

        // 3. Сохранение нового состояния
        stateData.setMenuState(newState);
        awaiting.put(user, stateData);

        // 4. Отправка сообщения нового состояния, если оно изменилось
        if (state != newState)
            stateRegistry.get(newState).executeOnState(bot, update, user);


        // 5. Удаление прошлого меню, чтобы не флудить сообщениями с кнопками (избыточно для пользователя)
        if (update.hasCallbackQuery())
            sender.deleteCallback(bot, user.getChatId(), update);
    }

    private void setCommandsForUser(UserData user) throws TelegramApiException {
        SetMyCommands setMyCommands = SetMyCommands
                .builder()
                .commands(BotCommandProvider.getDEF_COMMANDS())
                .scope(new BotCommandScopeChat(user.getChatId().toString()))
                .build();
        bot.execute(setMyCommands);
    }
}
