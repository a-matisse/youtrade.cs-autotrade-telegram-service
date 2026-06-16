package cs.youtrade.autotrade.client.telegram.prototype.sender.doc;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.sender.BaseSendMessageService;
import cs.youtrade.telegram.buttons.sender.doc.BaseDocMessageSender;
import org.springframework.stereotype.Service;

@Service
public class UserDocMessageSender extends BaseDocMessageSender<UserData> {
    public UserDocMessageSender(BaseSendMessageService sender) {
        super(sender);
    }
}
