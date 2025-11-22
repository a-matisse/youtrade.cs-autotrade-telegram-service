package cs.youtrade.autotrade.client.telegram.menu.main.mcreate.csource;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.mcreate.ParamsCreateData;
import cs.youtrade.autotrade.client.telegram.menu.main.mcreate.ParamsCreateRegistry;
import cs.youtrade.autotrade.client.telegram.menu.main.mcreate.parent.AbstractCreateState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class CreateSourceState extends AbstractCreateState {
    public CreateSourceState(
            UserTextMessageSender sender,
            ParamsCreateRegistry registry
    ) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_CREATE_SOURCE;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData e) {
        long chatId = e.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение в меню (/menu).");
            return UserMenu.MAIN;
        }

        MarketType destination;
        String input = update.getMessage().getText();
        destination = findClosestMarketType(input);
        if (destination == null) {
            sender.sendTextMes(bot, chatId, String.format("#1: Не удалось распознать источник: %s", input));
            return UserMenu.MAIN;
        }

        var data = registry.getOrCreateBuilder(e, ParamsCreateData::new);
        data.setDestination(destination);
        return UserMenu.MAIN_PARAMETERS_CREATE_PROCEED;
    }

    @Override
    protected String getMessage() {
        return String.format("Пожалуйста, укажите путь продажи (Допустимые варианты: %s)...", getMarketNames());
    }
}
