package cs.youtrade.autotrade.client.telegram.messaging;

import cs.youtrade.telegram.buttons.sender.BaseSendMessageService;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class TelegramSenderConfiguration implements AutoCloseable {
    private BaseSendMessageService sender;

    @Bean
    public BaseSendMessageService createMessageSender() {
        this.sender = BaseSendMessageService
                .builder()
                .maxMessageLength(4096)
                .build();
        sender.run();
        return sender;
    }

    @PreDestroy
    @Override
    public void close() throws Exception {
        sender.close();
    }
}
