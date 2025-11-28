package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.update.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.update.UserAutoBuyUpdateData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.update.UserAutoBuyUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoBuyUpdateFieldState extends AbstractTextState {
    private final UserAutoBuyUpdateRegistry registry;

    public AutoBuyUpdateFieldState(
            UserTextMessageSender sender,
            UserAutoBuyUpdateRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return """
                Введите название поля для изменения...
                
                Для настройки автопокупки используйте следующие шаблоны:
                - minPrice — Минимальная цена
                - maxPrice — Максимальная цена
                - priceFactor — Коэффициент превышения над минимальной рыночной ценой
                - minPopularity — Минимальное количество продаж в месяц
                - maxPopularity — Максимальное количество продаж в месяц
                - minDaysHold — Минимальное количество дней, на которое должен удерживаться предмет
                - maxDaysHold — Максимальное количество дней, на которые возможно удержание предмета
                - correctionCoefficient — Коэффициент для поправки на количество дней ожидания
                - manipulationCoeff — Коэффициент для определения манипуляций на основе сильных колебаний цены
                - maxDuplicates — Задает максимальное количество одинаковых предметов для ограничения дублирования покупок
                - duplicateLag — Задает максимальное количество одинаковых предметов для ограничения дублирования покупок
                - minTrendScore — Задает минимальный наклон тренда для покупаемого предмета
                - maxTrendScore — Задает минимальный наклон тренда для покупаемого предмета
                """;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.AUTOBUY;
        }

        String field = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoBuyUpdateData::new);
        data.setField(field);
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_2;
    }
}
