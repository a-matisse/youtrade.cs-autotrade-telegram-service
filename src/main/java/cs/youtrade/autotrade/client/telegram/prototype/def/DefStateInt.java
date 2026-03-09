package cs.youtrade.autotrade.client.telegram.prototype.def;

import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface DefStateInt<
        USER extends AbstractUserData,
        MENU extends Enum<MENU>,
        MESSAGE
        > {
    /**
     * Какое состояние поддерживает эта команда (ключ для registry)
     */
    MENU supportedState();

    /**
     * Выполнить команду. Реализация получает Update и выгруженную сущность tgEntity
     */
    MENU execute(TelegramClient bot, Update update, USER user);

    /**
     * Сообщение, которое отправится пользователю при смене состояния
     */
    void executeOnState(TelegramClient bot, USER e, Update update);

    /**
     * Метод создания сообщения
     */
    MESSAGE buildMessage(TelegramClient bot, USER e);

    /**
     * Сообщение, которое отправится пользователю при смене состояния (с данными)
     */
    void executeOnState(TelegramClient bot, USER user, UserStateData lastState, Object data);

    MESSAGE buildMessage(TelegramClient bot, USER e, Object data);
}
