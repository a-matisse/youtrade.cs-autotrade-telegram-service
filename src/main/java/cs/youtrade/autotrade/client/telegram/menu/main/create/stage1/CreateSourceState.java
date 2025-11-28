package cs.youtrade.autotrade.client.telegram.menu.main.create.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.create.ParamsCreateData;
import cs.youtrade.autotrade.client.telegram.menu.main.create.ParamsCreateRegistry;
import cs.youtrade.autotrade.client.telegram.menu.main.create.prototype.AbstractCreateState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class CreateSourceState extends AbstractCreateState {
    public CreateSourceState(
            UserTextMessageSender sender,
            ParamsCreateRegistry registry
    ) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_CREATE_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
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

        var data = registry.getOrCreate(user, ParamsCreateData::new);
        data.setDestination(destination);
        return UserMenu.MAIN_PARAMETERS_CREATE_STAGE_2;
    }

    @Override
    protected String getMessage(UserData user) {
        return String.format("Пожалуйста, укажите путь продажи (Допустимые варианты: %s)...", getMarketNames());
    }
}
