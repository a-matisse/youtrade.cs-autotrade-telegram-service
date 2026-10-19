package cs.youtrade.autotrade.client.telegram.menu.start.topup.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayData;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserPayAmountState extends YTPTextMenuState<UserPayAmountMenu> {
    private final UserPayRegistry registry;

    public UserPayAmountState(
            UserTextMessageSender sender,
            UserPayRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "topup.UserPayAmount");
        return String.format("""
                        %s <b>%s</b>

                        <blockquote>%s %s
                        %s %s</blockquote>

                        %s <b>%s</b>
                        """,
                        DynamicEmoji.MONEY.getEmoji(),
                        translations.get("top-up-your-y-cs-balance"),
                        DynamicEmoji.YOUTRADE.getEmoji(),
                        translations.format("your-y-cs-balance-b-pays-for", Map.of("emphasis", String.format("<b>%s</b>", translations.get("your-y-cs-balance-b-pays-for.emphasis")))),
                        DynamicEmoji.SECURE.getEmoji(),
                        translations.format("it-on-connected-marketplaces", Map.of("value1", String.format("<b>%s</b>", translations.get("is-not-used-to-buy-or-sell")))),
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("choose-an-amount-below-or-send-your"));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOP_UP_STAGE_1;
    }

    @Override
    public UserPayAmountMenu getOption(String optionStr) {
        return UserPayAmountMenu.valueOf(optionStr);
    }

    @Override
    public UserPayAmountMenu[] getOptions(UserData userData) {
        return UserPayAmountMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, UserPayAmountMenu option) {
        return prepare(user, option.getAmount());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "topup.UserPayAmount");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
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

        return prepare(user, amount);
    }

    private UserMenu prepare(UserData user, double amount) {
        var data = registry.getOrCreate(user, UserPayData::new);
        data.setAmount(amount);
        return UserMenu.TOP_UP_STAGE_P;
    }
}
