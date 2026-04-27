package cs.youtrade.autotrade.client.telegram.messaging.receiver;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.connect.RefConnectRegistry;
import cs.youtrade.autotrade.client.telegram.messaging.BotCommandProvider;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.StateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.UserInitializer;
import cs.youtrade.autotrade.client.telegram.prototype.UserRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.redis.IRedisConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class TelegramUpdReceiverService implements IRedisConsumer<Update> {
    private final TelegramClient bot;
    private final BotCommandProvider provider;
    private final StateRegistry stateRegistry;
    private final UserRegistry userRegistry;
    private final RefConnectRegistry registry;
    private final GeneralEndpoint endpoint;
    private UserInitializer userInitializer;
    private final Set<Long> initMap;

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
        // 2. Инициализация пользователя (если он не инициализирован)
        if (!initMap.contains(chatId)) {
            endpoint.initUser(chatId);
            initMap.add(chatId);
        }
        // 3. Выполнение запроса
        try {
            UserData user = userRegistry.getOrCreateUser(chatId, userInitializer::initUser);
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

        // 1. Проверка на возможную команду из меню
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String[] tokens = text.split("\\s+");
            if (tokens.length > 0) {
                String menu = tokens[0];
                UserMenu newMenu = provider.getCommandByCmd(menu);
                if (newMenu != null) {
                    // Проверка соответствия меню
                    // потому что в /start может быть передан промокод
                    if (newMenu == UserMenu.START && tokens.length > 1) {
                        // Проверка, что промокод вообще был передан
                        String param = tokens[1];
                        // Проверка, что промокод не пустой
                        if (param.startsWith("promo_") && param.length() > 6) {
                            String referralCode = param.substring(6);
                            registry.setReferral(user, referralCode);
                            newMenu = UserMenu.REF_CONNECT_STAGE_P;
                        }
                    }
                    stateData.setMenuState(newMenu);
                    user.setUpdated(true);
                }
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
