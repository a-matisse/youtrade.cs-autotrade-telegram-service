package cs.youtrade.autotrade.client.telegram.prototype.sender.doc;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import org.springframework.stereotype.Service;

@Service
public class UserDocMessageSender extends AbstrDocMessageSender<UserData>{
    public UserDocMessageSender(TelegramSendMessageService sender) {
        super(sender);
    }
}
