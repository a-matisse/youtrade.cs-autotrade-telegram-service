package cs.youtrade.autotrade.client.telegram.prototype.def;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface DefStateInt<USER, MENU extends Enum<MENU>> {
    /**
     * Какое состояние поддерживает эта команда (ключ для registry)
     */
    MENU supportedState();

    /**
     * Выполнить команду. Реализация получает Update и выгруженную сущность tgEntity
     */
    MENU execute(TelegramClient bot, Update update, USER e);

    /**
     * Сообщение, которое отправится пользователю при смене состояния
     */
    void executeOnState(TelegramClient bot, Update update, USER e);
}
