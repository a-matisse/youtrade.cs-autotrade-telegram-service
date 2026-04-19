package cs.youtrade.autotrade.client.telegram.prototype.sender.image;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.sender.ISenderService;
import cs.youtrade.telegram.buttons.sender.img.BaseImageMessageSender;
import org.springframework.stereotype.Service;

@Service
public class UserImageMessageSender extends BaseImageMessageSender<UserData> {
    public UserImageMessageSender(ISenderService sender) {
        super(sender);
    }
}
