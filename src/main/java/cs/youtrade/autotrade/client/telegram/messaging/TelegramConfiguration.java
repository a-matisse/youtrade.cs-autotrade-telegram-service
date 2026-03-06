package cs.youtrade.autotrade.client.telegram.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TelegramConfiguration {
    @Bean
    public TelegramClient createClient(
            @Value("${tg.token.main}") String botToken
    ) {
        return new OkHttpTelegramClient(botToken);
    }
}
