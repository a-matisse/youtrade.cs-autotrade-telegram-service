package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.GetNewestItemsData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.GetNewestItemsRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class GetNewestItemsHrsState extends YTPTextMenuState<GetNewestItemsHrsMenu> {
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
    public GetNewestItemsHrsMenu getOption(String optionStr) {
        return GetNewestItemsHrsMenu.valueOf(optionStr);
    }

    @Override
    public GetNewestItemsHrsMenu[] getOptions(UserData userData) {
        return GetNewestItemsHrsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, GetNewestItemsHrsMenu option) {
        return prepare(user, option.getHours());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
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

        return prepare(user, hrs);
    }

    private UserMenu prepare(UserData user, int hrs) {
        var data = registry.getOrCreate(user, GetNewestItemsData::new);
        data.setHrs(hrs);
        return UserMenu.AUTOBUY_GET_NEWEST_ITEMS_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Новые предметы</b>

                        <blockquote>%s <b>Excel-таблица</b> со всеми предметами, появившимися за выбранный период.
                        %s Максимальный период — <b>24 часа</b>.</blockquote>

                        %s <b>Выберите период ниже или отправьте своё количество часов</b>
                        """,
                DynamicEmoji.BOX.getEmoji(),
                DynamicEmoji.EXCEL.getEmoji(),
                DynamicEmoji.CLOCK.getEmoji(),
                DynamicEmoji.WRITE.getEmoji()
        );
    }
}
