package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage3;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendTerminalState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoSendDestinationState extends AbstractMarketCsgoSendTerminalState {
    public MarketCsgoSendDestinationState(UserTextMessageSender sender, MarketCsgoSendRegistry registry) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_DESTINATION; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Отправьте API-ключ аккаунта получателя</b>
                """, summary(user), DynamicEmoji.WRITE.getEmoji());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        String key = update.getMessage().getText().trim();
        if (key.isEmpty()) {
            sender.sendTextMes(bot, user, "API-ключ получателя не может быть пустым.");
            return supportedState();
        }
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        data.setDestinationKey(key);
        data.setDestinationProvided(true);
        return UserMenu.ACCOUNTS_SEND_AMOUNT;
    }
}
