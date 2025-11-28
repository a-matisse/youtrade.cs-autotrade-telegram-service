package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserStartState extends AbstractTextMenuState<UserTextMenu> {
    public UserStartState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public UserTextMenu getOption(String optionStr) {
        return UserTextMenu.valueOf(optionStr);
    }

    @Override
    public UserTextMenu[] getOptions() {
        return UserTextMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserTextMenu t) {
        return switch (t) {
            case MAIN -> UserMenu.MAIN;
            case TOP_UP -> UserMenu.TOP_UP_STAGE_1;
            case GET_PRICE -> UserMenu.GET_PRICE;
        };
    }

    @Override
    public String getHeaderText(UserData userData) {
        return """
                👋 Добро пожаловать в YouTrade.CS - AutoTrade!
                🤖 Автоматизированные продажи CS2 предметов
                
                Выберите одну из опций ниже:
                """;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.START;
    }
}
