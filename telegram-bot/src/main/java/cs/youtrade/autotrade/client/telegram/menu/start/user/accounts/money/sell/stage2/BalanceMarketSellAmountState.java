package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.AbstractBalanceMarketSellState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellText;

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
public class BalanceMarketSellAmountState extends AbstractBalanceMarketSellState<BalanceMarketSellAmountMenu> {
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("500");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");

    public BalanceMarketSellAmountState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                        BalanceMarketSellText text, BotTexts botTexts) {
        super(sender, registry, text, botTexts);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL_AMOUNT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellAmount");
        return String.format("""
                %s%s <b>%s</b>
                └ <i>%s: USD.</i>
                """,
                text.summary(registry.getOrCreate(user, BalanceMarketSellData::new), translations.inBundle("accounts.money-sell.BalanceMarketSellCommon")),
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("choose-an-amount-or-send-it-as"),
                translations.get("from-500-to-10-000-up-to"));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellAmount");
        if (!hasText(bot, update, user)) return supportedState();
        try {
            return choose(bot, user, new BigDecimal(update.getMessage().getText().trim().replace(',', '.')));
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, String.format("%s: <code>500.00</code>",
                    translations.get("could-not-read-the-amount-example")));
            return supportedState();
        }
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, BalanceMarketSellAmountMenu option) {
        if (option == BalanceMarketSellAmountMenu.RETURN) return leave(user);
        return switch (option) {
            case AMOUNT_500 -> choose(bot, user, MIN_AMOUNT);
            case AMOUNT_1000 -> choose(bot, user, new BigDecimal("1000"));
            case ALL -> choose(bot, user, registry.getOrCreate(user, BalanceMarketSellData::new).getAvailable().min(MAX_AMOUNT).setScale(3, RoundingMode.DOWN));
            default -> supportedState();
        };
    }

    private UserMenu choose(TelegramClient bot, UserData user, BigDecimal amount) {
        var translations = botTexts.forUser(user, "accounts.money-sell.BalanceMarketSellAmount");
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        if (amount.signum() <= 0 || amount.scale() > 3 || amount.compareTo(MIN_AMOUNT) < 0
                || amount.compareTo(MAX_AMOUNT) > 0) {
            sender.sendTextMes(bot, user, translations.get("the-amount-must-be-from-500-to"));
        } else if (amount.compareTo(data.getAvailable()) > 0) {
            sender.sendTextMes(bot, user, String.format("%s: $",
                    translations.get("the-amount-exceeds-the-available-balance")) + text.money(data.getAvailable()) + ".");
        } else {
            data.setAmount(amount);
            return UserMenu.ACCOUNTS_SELL_METHOD;
        }
        return supportedState();
    }

    @Override
    public BalanceMarketSellAmountMenu getOption(String value) {
        return BalanceMarketSellAmountMenu.valueOf(value);
    }

    @Override
    public BalanceMarketSellAmountMenu[] getOptions(UserData user) {
        return BalanceMarketSellAmountMenu.values();
    }
}
