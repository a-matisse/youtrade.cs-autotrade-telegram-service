package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage4;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendTransfer;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MarketCsgoSendAmountState extends AbstractMarketCsgoSendState<MarketCsgoSendAmountMenu> {
    private final MarketCsgoSendTransfer transfer;

    public MarketCsgoSendAmountState(UserTextMessageSender sender, MarketCsgoSendRegistry registry,
                                     MarketCsgoSendTransfer transfer, BotTexts botTexts) {
        super(sender, registry, botTexts);
        this.transfer = transfer;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_AMOUNT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSendAmount");
        return String.format("""
                %s%s <b>%s</b>
                └ <i>«%s</i>
                """,
                summary(user),
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("choose-an-amount-or-send-it-as"),
                translations.get("all-means-the-available-balance-including-the"));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSendAmount");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        try {
            return choose(bot, user, new BigDecimal(update.getMessage().getText().trim().replace(',', '.')));
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, String.format("%s: <code>200.00</code>",
                    translations.get("could-not-read-the-amount-example")));
            return supportedState();
        }
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, MarketCsgoSendAmountMenu option) {
        if (option == MarketCsgoSendAmountMenu.RETURN) return leave(user);
        return switch (option) {
            case AMOUNT_200 -> choose(bot, user, new BigDecimal("200"));
            case AMOUNT_500 -> choose(bot, user, new BigDecimal("500"));
            case AMOUNT_1000 -> choose(bot, user, new BigDecimal("1000"));
            case ALL -> choose(bot, user, registry.getOrCreate(user, MarketCsgoSendData::new).getAvailable().setScale(3, RoundingMode.DOWN));
            default -> supportedState();
        };
    }

    private UserMenu choose(TelegramClient bot, UserData user, BigDecimal amount) {
        var translations = botTexts.forUser(user, "accounts.money-send.MarketCsgoSendAmount");
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        if (amount.signum() <= 0 || amount.scale() > 3) {
            sender.sendTextMes(bot, user, translations.get("the-amount-must-be-positive-and-have"));
        } else if (amount.compareTo(transfer.testAmount()) < 0) {
            sender.sendTextMes(bot, user, translations.get("minimum-amount-including-the-recipient-check")
                    + transfer.testAmount().toPlainString() + ".");
        } else if (amount.compareTo(data.getAvailable()) > 0) {
            sender.sendTextMes(bot, user, String.format("%s: $",
                    translations.get("the-amount-exceeds-the-available-balance")) + money(data.getAvailable()) + ".");
        } else {
            data.setAmount(amount);
            return UserMenu.ACCOUNTS_SEND_CONFIRM;
        }
        return supportedState();
    }

    @Override
    public MarketCsgoSendAmountMenu getOption(String value) {
        return MarketCsgoSendAmountMenu.valueOf(value);
    }

    @Override
    public MarketCsgoSendAmountMenu[] getOptions(UserData user) {
        return MarketCsgoSendAmountMenu.values();
    }
}
