package cs.youtrade.autotrade.client.telegram.prototype.sender.text;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import org.springframework.stereotype.Service;

@Service
public class UserTextMessageSender extends AbstrTextMessageSender<UserData> {
    public UserTextMessageSender(TelegramSendMessageService sender) {
        super(sender);
    }
}
