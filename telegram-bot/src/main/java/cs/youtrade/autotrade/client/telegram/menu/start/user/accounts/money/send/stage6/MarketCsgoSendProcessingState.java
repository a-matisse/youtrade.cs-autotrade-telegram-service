package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage6;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Log4j2
public class MarketCsgoSendProcessingState extends AbstractMarketCsgoSendState<MarketCsgoSendProcessingMenu> {
    public MarketCsgoSendProcessingState(UserTextMessageSender sender, MarketCsgoSendRegistry registry) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_PROCESSING; }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, MarketCsgoSendProcessingMenu option) {
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        if (data.isProcessing()) return supportedState();
        return data.getResult() == null ? UserMenu.ACCOUNTS_SEND_VERIFY : UserMenu.ACCOUNTS_SEND_RESULT;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Проверяем результат перевода...</b>
                """, summary(user), DynamicEmoji.WAIT.getEmoji());
    }

    public void show(TelegramClient bot, UserData user) {
        try {
            bot.execute(buildEdit(bot, user));
        } catch (TelegramApiException | RuntimeException error) {
            log.warn("Couldn't show money transfer progress: {}", error.getMessage());
        }
    }

    @Override
    public MarketCsgoSendProcessingMenu getOption(String value) {
        return MarketCsgoSendProcessingMenu.valueOf(value);
    }

    @Override
    public MarketCsgoSendProcessingMenu[] getOptions(UserData user) {
        return MarketCsgoSendProcessingMenu.values();
    }
}
