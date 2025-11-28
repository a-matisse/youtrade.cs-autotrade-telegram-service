package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.update.stage2;

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
public class AutoBuyUpdateValueState extends AbstractTextState {
    private final UserAutoBuyUpdateRegistry registry;

    public AutoBuyUpdateValueState(
            UserTextMessageSender sender,
            UserAutoBuyUpdateRegistry registry
    ) {

        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return """
                Теперь укажите значение для выбранного поля...
                
                Для настройки автопокупки используйте следующие шаблоны:
                - minPrice — Допустимая цена: от $0 (Пример: 20)
                - maxPrice — Допустимая цена: от $0 и не меньше minPrice (Пример: 150)
                - priceFactor — Допустимое значение коэффициента: от 10% до 1000% (Пример: 110)
                - minPopularity — Допустимое значение продаж в месяц: от 0 продаж (Пример: 50)
                - maxPopularity — Допустимое значение продаж в месяц: от 0 продаж и не меньше minPopularity (Пример: 500)
                - minDaysHold — Допустимое значение количества дней: от 0 дн. до maxDaysHold
                - maxDaysHold — Допустимое значение количества дней: от minDaysHold до 8 дн.
                - correctionCoefficient — Допустимое значение коэффициента: от 1% до 100%
                - manipulationCoeff — Допустимое значение коэффициента: от 1% до 100%
                - maxDuplicates — Допустимое количество дубликатов: от 0 шт.
                - duplicateLag — Допустимая для анализа дублирования: от 1 дн.
                - minTrendScore — Допустимый наклон: от -100%
                - maxTrendScore — Допустимый наклон: от -100% и больше minTrendScore
                """;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.AUTOBUY;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoBuyUpdateData::new);
        data.setValue(value);
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_P;
    }
}
