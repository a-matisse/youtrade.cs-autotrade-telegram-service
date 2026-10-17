package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage2;

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
public class MarketCsgoSendPasswordState extends AbstractMarketCsgoSendTerminalState {
    public MarketCsgoSendPasswordState(UserTextMessageSender sender, MarketCsgoSendRegistry registry) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_PASSWORD; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Отправьте платёжный пароль Market.CSGO</b>
                """, summary(user), DynamicEmoji.WRITE.getEmoji());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        String password = update.getMessage().getText().trim();
        if (password.isEmpty()) {
            sender.sendTextMes(bot, user, "Платёжный пароль не может быть пустым.");
            return supportedState();
        }
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        data.setPassword(password);
        data.setPasswordProvided(true);
        return UserMenu.ACCOUNTS_SEND_DESTINATION;
    }
}
