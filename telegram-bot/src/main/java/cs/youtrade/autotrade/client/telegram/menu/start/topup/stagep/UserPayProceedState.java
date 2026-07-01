package cs.youtrade.autotrade.client.telegram.menu.start.topup.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdTopUpDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.telegram.buttons.menu.InlineKeyboardButtonStyle;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class UserPayProceedState extends YTPTextMenuState<UserPayProceedMenu> {
    private static final Map<UserData, FcdTopUpDto> subMap = new ConcurrentHashMap<>();

    private final UserPayRegistry registry;
    private final SubGetEndpoint endpoint;

    public UserPayProceedState(
            UserTextMessageSender sender,
            UserPayRegistry registry,
            SubGetEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOP_UP_STAGE_P;
    }

    @Override
    public UserPayProceedMenu getOption(String optionStr) {
        return UserPayProceedMenu.valueOf(optionStr);
    }

    @Override
    public UserPayProceedMenu[] getOptions(UserData userData) {
        return UserPayProceedMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserPayProceedMenu t) {
        return switch (t) {
            case PAY, RETURN -> UserMenu.START;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.topUp(user.getChatId(), data.getAmount());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        subMap.put(user, fcd);
        return String.format("""
                    %s <b>Пополнение баланса</b>
                    
                    • ID пользователя: <b><code>%d</code></b>
                    • Сумма: <b>$%.2f</b> <i>(<b>≈ %.2f ₽</b>)</i>
                    • Оператор: <b>%s</b>
                    
                    <blockquote>%s <b>Безопасный платёж</b>
                    • Платёжный партнёр: <b>HeleketPay</b>
                    • Стандартная AML-проверка
                    
                    %s <i>Ссылка активна 60 минут</i>
                    %s <i>Баланс зачисляется автоматически</i></blockquote>
                    """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                fcd.getUserTdId(),
                fcd.getUsdAmount(),
                fcd.getRubAmount(),
                fcd.getType(),
                DynamicEmoji.SECURE.getEmoji(),
                DynamicEmoji.WAIT.getEmoji(),
                DynamicEmoji.FAST.getEmoji()
        );
    }

    @Override
    public Map<UserPayProceedMenu, String> getUrls(UserData user) {
        var fcd = subMap.get(user);
        return Map.of(
                UserPayProceedMenu.PAY, fcd.getUrl()
        );
    }

    @Override
    public Map<UserPayProceedMenu, Function<UserData, InlineKeyboardButtonStyle>> getButtonStyle(UserData userData) {
        return Map.of(
                UserPayProceedMenu.PAY, u -> InlineKeyboardButtonStyle.PRIMARY
        );
    }

    @Override
    public void executeSide(TelegramClient bot, Update update, UserData userData) {
        FcdTopUpDto ans = subMap.remove(userData);
        String notification = getNotification(update, userData, ans);
        ans.getAdminChats().forEach(adminChatId ->
                sender.sendTextMes(bot, new UserData(adminChatId), notification));
    }

    private String getNotification(Update update, UserData userData, FcdTopUpDto ans) {
        long tdId = ans.getUserTdId();
        long chatId = userData.getChatId();
        String username = String.format("[@%s]", update.getCallbackQuery().getFrom().getUserName());

        return String.format(
                "Пользователь %s с ID=%d запросил пополнение на сумму $%s (₽%s) (chatId=%d)",
                username,
                tdId,
                ans.getUsdAmount().toPlainString(),
                ans.getRubAmount().toPlainString(),
                chatId
        );
    }
}
