package cs.youtrade.autotrade.client.telegram.prototype.menu;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractDefState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.MessageSenderInt;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public abstract class AbstractMenuState<MENU_TYPE extends IMenuEnum, MESSAGE>
        extends AbstractDefState<UserData, MESSAGE>
        implements MenuStateInt<UserData, MENU_TYPE, UserMenu> {
    public AbstractMenuState(
            MessageSenderInt<UserData, MESSAGE> sender
    ) {
        super(sender);
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData userData) {
        // execute side
        executeSide(bot, update, userData);

        if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();
            try {
                sender.replyCallback(bot, update, userData);
                MENU_TYPE menuType = getOption(callbackQuery.toUpperCase());
                return executeCallback(bot, update, userData, menuType);
            } catch (Exception e) {
                log.error("Ошибка в callback: {}", e.getMessage());
            }
        }

        if (update.hasMessage() && update.getMessage().hasText())
            sender.sendMessage(bot, userData, buildMessage(userData));

        return supportedState();
    }

    @Override
    public List<InlineKeyboardRow> buildKeyboard() {
        return Arrays
                .stream(getOptions())
                .collect(Collectors.groupingBy(IMenuEnum::getRowNum))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new InlineKeyboardRow(entry
                        .getValue()
                        .stream()
                        .map(menuOption ->
                                InlineKeyboardButton
                                        .builder()
                                        .text(menuOption.getButtonName())
                                        .callbackData(menuOption.toString())
                                        .build()
                        )
                        .toList()
                ))
                .toList();
    }

    @Override
    public InlineKeyboardMarkup buildMarkup() {
        return InlineKeyboardMarkup.builder()
                .keyboard(buildKeyboard())
                .build();
    }

    public void executeSide(TelegramClient bot, Update update, UserData userData) {
    }
}
