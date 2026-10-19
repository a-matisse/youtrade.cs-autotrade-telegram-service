package cs.youtrade.autotrade.client.telegram.menu.notification.balance;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTNotificationMenu;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.YTBalanceNotification;
import cs.youtrade.autotrade.client.util.notification.YTNotificationType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class YTBalanceNotifyState extends YTNotificationMenu<YTBalanceNotifyMenu, YTBalanceNotification> {
    private static final Double STANDARD_PAYMENT = 40d;
    private final UserPayRegistry payRegistry;

    public YTBalanceNotifyState(
            UserTextMessageSender sender,
            UserPayRegistry payRegistry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
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
    public YTBalanceNotifyMenu[] getOptions(UserData userData) {
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
        var translations = botTexts.forUser(user, "notification.BalanceNotifications");
        return String.format("""
                        %s <b>%s</b>
                        
                        %s <b>%s</b>
                        <blockquote>• ID: <b>%s</b>
                        • %s → <tg-spoiler><b>$%.2f</b></tg-spoiler></blockquote>
                        
                        %s %s
                        """,
                        DynamicEmoji.WARNING.getEmoji(),
                        translations.get("your-balance-is-running-low"),
                        DynamicEmoji.PROFILE.getEmoji(),
                        translations.get("profile"),
                        data.getTdId(),
                        translations.get("balance"),
                        data.getBalance(),
                        DynamicEmoji.UP.getEmoji(),
                        translations.format("to-reveal-your-balance", Map.of("value1", String.format("<b>%s</b>", translations.get("tap-above")))));
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
