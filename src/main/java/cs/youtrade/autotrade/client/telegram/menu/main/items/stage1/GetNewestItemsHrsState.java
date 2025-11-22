package cs.youtrade.autotrade.client.telegram.menu.main.items.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.items.GetNewestItemsData;
import cs.youtrade.autotrade.client.telegram.menu.main.items.GetNewestItemsRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class GetNewestItemsHrsState extends AbstractTextState {
    private final GetNewestItemsRegistry registry;

    public GetNewestItemsHrsState(
            UserTextMessageSender sender,
            GetNewestItemsRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_GET_NEWEST_ITEMS_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение в меню (/menu).");
            return UserMenu.MAIN;
        }

        String input = update.getMessage().getText();
        int hrs;
        try {
            hrs = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            sender.sendTextMes(bot, chatId, String.format("#1: Не удалось распознать число: %s", input));
            return UserMenu.MAIN;
        }

        var data = registry.getOrCreate(user, GetNewestItemsData::new);
        data.setHrs(hrs);
        return UserMenu.MAIN_GET_NEWEST_ITEMS_STAGE_2;
    }

    @Override
    protected String getMessage() {
        return """
                ⏰ Введите количество часов
                📊 Максимум: 24 часа
                💡 Пример: 1, 6, 12, 24
                """;
    }
}
