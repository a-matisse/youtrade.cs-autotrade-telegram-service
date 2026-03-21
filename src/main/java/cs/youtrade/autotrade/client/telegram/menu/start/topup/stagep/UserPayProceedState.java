package cs.youtrade.autotrade.client.telegram.menu.start.topup.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdTopUpDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
                    💸 <b>Пополнение баланса</b>
                    ━━━━━━━━━━━━━━━━━━━━━
                    
                    <b>ID:</b> %d
                    <b>Сумма:</b> $%.2f <i>(≈ %.2f ₽)</i>
                    <b>Тип:</b> %s
                    
                    ━━━━━━━━━━━━━━━━━━━━━
                    🔐 <b>Безопасный платёж</b>
                    • Платёжный партнёр: <b>HeleketPay</b>
                    • Стандартная AML-проверка
                    • Никакие данные аккаунта не запрашиваются
                    
                    ⏳ <i>Ссылка активна 60 минут</i>
                    ⚡ <i>Баланс зачисляется автоматически</i>
                    """,
                fcd.getUserTdId(),
                fcd.getUsdAmount(),
                fcd.getRubAmount(),
                fcd.getType()
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
