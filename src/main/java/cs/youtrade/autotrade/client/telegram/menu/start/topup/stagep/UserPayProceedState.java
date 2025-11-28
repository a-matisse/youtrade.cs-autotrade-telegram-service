package cs.youtrade.autotrade.client.telegram.menu.start.topup.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdSubGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserPayProceedState extends AbstractTerminalTextMenuState {
    private static final Map<UserData, FcdSubGetDto> subMap = new ConcurrentHashMap<>();

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
    public String getHeaderText(UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.topUp(user.getChatId(), data.getAmount());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        subMap.put(user, fcd);
        return String.format("Ваш запрос на полные права доступа отправлен администратору. Ваш user-ID=%d", fcd.getUserTdId());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.START;
    }

    @Override
    public void executeSide(TelegramClient bot, Update update, UserData userData) {
        FcdSubGetDto ans = subMap.remove(userData);
        String notification = getNotification(update, userData, ans);
        ans.getAdminChats().forEach(adminChatId ->
                sender.sendTextMes(bot, adminChatId, notification));
    }

    private String getNotification(Update update, UserData userData, FcdSubGetDto ans) {
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
