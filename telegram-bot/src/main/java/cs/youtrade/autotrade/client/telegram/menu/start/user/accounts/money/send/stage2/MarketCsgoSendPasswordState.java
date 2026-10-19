package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
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
    public MarketCsgoSendPasswordState(UserTextMessageSender sender, MarketCsgoSendRegistry registry, BotTexts botTexts) {
        super(sender, registry, botTexts);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_PASSWORD; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSendPassword");
        return String.format("""
                %s%s <b>%s</b>
                """,
                summary(user),
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("send-your-market-csgo-payment-password"));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSendPassword");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        String password = update.getMessage().getText().trim();
        if (password.isEmpty()) {
            sender.sendTextMes(bot, user, translations.get("the-payment-password-cannot-be-empty"));
            return supportedState();
        }
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        data.setPassword(password);
        data.setPasswordProvided(true);
        return UserMenu.ACCOUNTS_SEND_DESTINATION;
    }
}
