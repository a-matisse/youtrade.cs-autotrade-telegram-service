package cs.youtrade.autotrade.client.telegram.prototype.sender.text;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.sender.BaseSendMessageService;
import cs.youtrade.telegram.buttons.sender.text.BaseTextMessageSender;
import org.springframework.stereotype.Service;

@Service
public class UserTextMessageSender extends BaseTextMessageSender<UserData> {
    public UserTextMessageSender(BaseSendMessageService sender) {
        super(sender);
    }
}
