package cs.youtrade.autotrade.client.telegram.messaging;

import cs.youtrade.telegram.buttons.sender.BaseSendMessageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TelegramSendMessageService extends BaseSendMessageService {
    public TelegramSendMessageService() {
        super(4096);
    }

    @PostConstruct
    public void init() {
        run();
    }
}
