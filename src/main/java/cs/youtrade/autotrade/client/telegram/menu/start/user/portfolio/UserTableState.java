package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserTableState extends AbstractTextMenuState<UserTableMenu> {
    public UserTableState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO;
    }

    @Override
    public UserTableMenu getOption(String optionStr) {
        return UserTableMenu.valueOf(optionStr);
    }

    @Override
    public UserTableMenu[] getOptions() {
        return UserTableMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserTableMenu t) {
        return switch (t) {
            case TABLE_SELLING -> UserMenu.PORTFOLIO_SELLING_STAGE_1;
            case TABLE_WAITING -> UserMenu.PORTFOLIO_WAITING;
            case TABLE_HISTORY -> UserMenu.PORTFOLIO_HISTORY_STAGE_1;
            case TABLE_UPLOAD -> UserMenu.PORTFOLIO_UPLOAD_STAGE_1;
            case TABLE_CHANGE -> UserMenu.PORTFOLIO_CHANGE_STAGE_CHOOSE;
            case TABLE_RESTRICT -> UserMenu.PORTFOLIO_RESTRICT_STAGE_1;
            case RETURN -> UserMenu.USER;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return "🏪 Управление таблицей продаж - Выберите действие:";
    }
}
