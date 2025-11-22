package cs.youtrade.autotrade.client.telegram.prototype.menu;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractDefState;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.List;

@Log4j2
public abstract class AbstractMenuState<MENU_TYPE extends MenuEnumInterface, MESSAGE>
        extends AbstractDefState<MESSAGE>
        implements MenuStateInt<UserData, MENU_TYPE, UserMenu> {
    public AbstractMenuState(
            TelegramSendMessageService sender
    ) {
        super(sender);
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData userData) {
        if (update.hasCallbackQuery()) {
            String callbackId = update.getCallbackQuery().getId();
            String callbackQuery = update.getCallbackQuery().getData();
            try {
                // Menu reply
                AnswerCallbackQuery ack = AnswerCallbackQuery
                        .builder()
                        .callbackQueryId(callbackId)
                        .build();
                sender.sendMessage(bot, userData.getChatId(), ack);

                MENU_TYPE menuType = getOption(callbackQuery.toUpperCase());
                return executeCallback(bot, update, userData, menuType);
            } catch (Exception e) {
                log.error("Ошибка в callback: {}", e.getMessage());
            }
        }

        if (update.hasMessage() && update.getMessage().hasText())
            sendMessage(bot, update, userData);

        return supportedState();
    }

    @Override
    public void executeOnState(TelegramClient bot, Update update, UserData userData) {
        sendMessage(bot, update, userData);
    }

    @Override
    public List<InlineKeyboardRow> buildKeyboard() {
        return Arrays.stream(getOptions())
                .map(value ->
                        new InlineKeyboardRow(
                                InlineKeyboardButton.builder()
                                        .text(value.getButtonName())
                                        .callbackData(value.toString())
                                        .build()
                        )
                )
                .toList();
    }

    @Override
    public InlineKeyboardMarkup buildMarkup() {
        return InlineKeyboardMarkup.builder()
                .keyboard(buildKeyboard())
                .build();
    }

    public void sendDefErrMes(TelegramClient bot, long chatId) {
        sender.sendMessage(bot, chatId, SERVER_ERROR_MES);
    }
}
