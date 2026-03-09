package cs.youtrade.autotrade.client.telegram.menu.notification.balance;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractNotificationMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.notification.YTBalanceNotification;
import cs.youtrade.autotrade.client.util.notification.YTNotificationType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class YTBalanceNotifyState extends AbstractNotificationMenuState<YTBalanceNotifyMenu, YTBalanceNotification> {
    private static final Double STANDARD_PAYMENT = 40d;
    private final UserPayRegistry payRegistry;

    public YTBalanceNotifyState(
            UserTextMessageSender sender,
            UserPayRegistry payRegistry
    ) {
        super(sender);
        this.payRegistry = payRegistry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.NOTIFICATION_BALANCE;
    }

    @Override
    public YTBalanceNotifyMenu getOption(String optionStr) {
        return YTBalanceNotifyMenu.valueOf(optionStr);
    }

    @Override
    public YTBalanceNotifyMenu[] getOptions() {
        return YTBalanceNotifyMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, YTBalanceNotifyMenu t) {
        return switch (t) {
            case PAY -> processStandardPayment(userData);
            case RETURN -> getReturnMenu(userData);
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user, YTBalanceNotification data) {
        return String.format("""
                ⚠️ <b>Ваш баланс близится к нулю</b>
                
                👤 <b>Профиль</b>
                <blockquote>• ID: <b>%s</b>
                • Баланс → <tg-spoiler><b>$%.2f</b></tg-spoiler></blockquote>
                
                👆 Нажмите, чтобы узнать точный остаток
                """,
                data.getTdId(),
                data.getBalance());
    }

    @Override
    public YTNotificationType getNotificationType() {
        return YTNotificationType.BALANCE;
    }

    private UserMenu processStandardPayment(UserData userData) {
        // ToDo: Сделать стандартный платёж
        //       ПРИМЕР:
        //       var userPay = payRegistry.getOrCreate(userData, UserPayData::new);
        //       userPay.setAmount(STANDARD_PAYMENT);
        //       return UserMenu.TOP_UP_STAGE_P;
        return UserMenu.TOP_UP_STAGE_1;
    }
}
