package cs.youtrade.autotrade.client.telegram.menu.start.topup.stage1;

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
            UserPayRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Пополнение баланса Y.CS</b>

                        <blockquote>%s <b>Баланс сервиса</b> оплачивает ReFill, Bargain, автопродажу и Worker.
                        %s Он <b>не используется для покупки или продажи предметов</b> на подключённых площадках.</blockquote>

                        %s <b>Выберите сумму ниже или отправьте свою в $ (USD)</b>
                        """,
                DynamicEmoji.MONEY.getEmoji(),
                DynamicEmoji.YOUTRADE.getEmoji(),
                DynamicEmoji.SECURE.getEmoji(),
                DynamicEmoji.WRITE.getEmoji()
        );
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
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.START;
        }

        String input = update.getMessage().getText();
        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount <= 0) {
                sender.sendTextMes(bot, user,
                        "#2: Введенное значение не является положительное числом");
                return UserMenu.START;
            }
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format(
                    "#1: Введенное значение не является положительное числом: %s", input));
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
