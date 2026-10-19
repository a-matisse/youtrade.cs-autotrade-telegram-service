package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.stage3;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.QuickConfigCreateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.QuickConfigCreateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.math.BigDecimal;

@Service
public class ConfigCreateBankSizeState extends YTPTextState {
    private final QuickConfigCreateRegistry registry;

    public ConfigCreateBankSizeState(
            UserTextMessageSender sender,
            QuickConfigCreateRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.USER_QUICK_CONFIG_INIT_STAGE_3;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.QuickConfig");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.START;
        }

        String input = update.getMessage().getText();
        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount <= 0) {
                sender.sendTextMes(bot, user,
                        String.format("#2: %s",
                                translations.get("the-value-entered-is-not-a-positive")));
                return UserMenu.START;
            }
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                            translations.get("the-value-entered-is-not-a-positive"),
                            input));
            return UserMenu.START;
        }

        var data = registry.getOrCreate(user, QuickConfigCreateData::new);
        data.setPreferredTradeCapital(BigDecimal.valueOf(amount));
        return UserMenu.USER_QUICK_CONFIG_INIT_STAGE_P;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.QuickConfig");
        return String.format("""
                💰 <b>%s</b>
                
                <blockquote><i>%s
                
                %s: <b>$<code>5000</code></b></i></blockquote>
                """,
                translations.get("trading-capital"),
                translations.format("enter-the-amount-allocated-to-trading-this", Map.of("value1", String.format("<b>%s</b>", translations.get("your-approximate-trading-capital-in-usd")), "value2", String.format("<b>%s</b>", translations.get("not-a-required-payment")), "value3", String.format("<b>%s</b>", translations.get("a-guide-to-the-scale-of-operations")))),
                translations.get("example"));
    }
}
