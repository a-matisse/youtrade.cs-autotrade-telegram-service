package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.PageMenu;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenuInt;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.function.Predicate;

public abstract class YTPPageTextMenuState extends YTPTextMenuState<PageMenu> implements TerminalMenuInt {
    public YTPPageTextMenuState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public PageMenu getOption(String optionStr) {
        return PageMenu.valueOf(optionStr);
    }

    @Override
    public PageMenu[] getOptions(UserData userData) {
        return PageMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, PageMenu t) {
        onPageCallback(bot, update, userData, t);
        return switch (t) {
            case PREVIOUS -> {
                onPreviousPage(bot, update, userData, t);
                yield supportedState();
            }
            case NEXT -> {
                onNextPage(bot, update, userData, t);
                yield supportedState();
            }
            case RETURN -> {
                onReturn(bot, update, userData, t);
                yield retState();
            }
        };
    }

    public void onPageCallback(TelegramClient bot, Update update, UserData userData, PageMenu t) {
    }

    public void onReturn(TelegramClient bot, Update update, UserData userData, PageMenu t) {
    }

    public abstract void onPreviousPage(TelegramClient bot, Update update, UserData userData, PageMenu t);

    public abstract void onNextPage(TelegramClient bot, Update update, UserData userData, PageMenu t);

    public abstract boolean hasNextPage(UserData userData);

    public abstract boolean hasPreviousPage(UserData userData);

    @Override
    public Map<PageMenu, Predicate<UserData>> getVisibilityPredicates(UserData userData) {
        return Map.of(
                PageMenu.NEXT, this::hasNextPage,
                PageMenu.PREVIOUS, this::hasPreviousPage
        );
    }
}
