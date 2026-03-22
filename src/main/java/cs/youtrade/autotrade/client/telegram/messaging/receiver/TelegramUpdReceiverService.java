package cs.youtrade.autotrade.client.telegram.messaging.receiver;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.BotCommandProvider;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.StateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.UserRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.util.redis.IRedisConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class TelegramUpdReceiverService implements IRedisConsumer<Update> {
    private final TelegramClient bot;
    private final BotCommandProvider provider;
    private final TelegramSendMessageService sender;
    private final StateRegistry stateRegistry;
    private final UserRegistry userRegistry;

    @Override
    public boolean shouldDeserialize() {
        return true;
    }

    @Override
    public boolean consume(String payload, Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery())
            return false;

        // 1. Поиск пользователя в системе
        Long chatId = update.hasMessage()
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId();

        // 2. Выполнение запроса
        try {
            UserData user = userRegistry.getUser(chatId);
            proceedTask(user, update);
        } catch (TelegramApiException e) {
            log.error("Couldn't proceed the update because of an error: {}", e.getMessage());
        }
        return true;
    }

    private void proceedTask(UserData user, Update update) throws TelegramApiException {
        UserStateData stateData = stateRegistry.getState(user);
        if (stateData == null)
            return;

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            UserMenu newMenu = provider.getCommandByCmd(text);
            if (newMenu != null) {
                stateData.setMenuState(newMenu);
                user.setUpdated(true);
            }
        }

        // 2. Выполнение команды
        UserMenu state = stateData.getMenuState();
        UserMenu newState;
        try {
            newState = stateRegistry.getMenu(state).execute(bot, update, user);
        } catch (Exception e) {
            log.error("Couldn't proceed the update because of an error: {}", e.getMessage(), e);
            newState = UserMenu.START;
        }

        // 3. Сохранение нового состояния
        stateData.setMenuState(newState);
        stateRegistry.put(user, stateData);

        // 4. Вывод сообщения нового состояния, если это не команда
        stateRegistry.getMenu(newState).executeOnState(bot, update, user);
    }
}
