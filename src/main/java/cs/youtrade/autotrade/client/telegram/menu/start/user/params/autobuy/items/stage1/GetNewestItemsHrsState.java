package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.GetNewestItemsData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.GetNewestItemsRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class GetNewestItemsHrsState extends YTPTextState {
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
        return UserMenu.AUTOBUY_GET_NEWEST_ITEMS_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение в меню (/menu).");
            return UserMenu.AUTOBUY;
        }

        String input = update.getMessage().getText();
        int hrs;
        try {
            hrs = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            sender.sendTextMes(bot, user, String.format("#1: Не удалось распознать число: %s", input));
            return UserMenu.AUTOBUY;
        }

        var data = registry.getOrCreate(user, GetNewestItemsData::new);
        data.setHrs(hrs);
        return UserMenu.AUTOBUY_GET_NEWEST_ITEMS_STAGE_P;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Введите количество часов (макс. 24 часа)</b>
                        └ <i>Пример: <code>1</code> • <code>6</code> • <code>12</code> • <code>24</code></i>
                        """,
                DynamicEmoji.CLOCK.getEmoji()
        );
    }
}
