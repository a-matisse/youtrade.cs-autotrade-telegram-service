package cs.youtrade.autotrade.client.telegram.menu.pay.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
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
public class UserPayState extends AbstractTerminalTextMenuState {
    private static final Map<UserData, FcdSubGetDto> subMap = new ConcurrentHashMap<>();

    private final SubGetEndpoint endpoint;

    public UserPayState(
            UserTextMessageSender sender,
            SubGetEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.START;
    }

    @Override
    public String getHeaderText(UserData userData) {
        var restAns = endpoint.getSub(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        subMap.put(userData, fcd);
        return String.format("Ваш запрос на полные права доступа отправлен. Ваш user-ID=%d", fcd.getUserTdId());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.MAIN;
    }

    @Override
    public void executeSide(TelegramClient bot, Update update, UserData userData) {
        FcdSubGetDto ans = subMap.remove(userData);
        String notification = getNotification(update, ans);
        ans.getAdminChats().forEach(adminChatId ->
                sender.sendTextMes(bot, adminChatId, notification));
    }

    private String getNotification(Update update, FcdSubGetDto ans) {
        long tdId = ans.getUserTdId();
        long chatId = update.getMessage().getChatId();
        String username = String.format("[@%s]", update.getMessage().getText());

        return String.format("Пользователь %s с ID=%d запросил полные права доступа (chatId=%d)",
                username, tdId, chatId);
    }
}
