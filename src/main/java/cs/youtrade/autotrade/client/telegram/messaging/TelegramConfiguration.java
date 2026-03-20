package cs.youtrade.autotrade.client.telegram.messaging;

import lombok.extern.log4j.Log4j2;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Configuration
@Log4j2
public class TelegramConfiguration {
    @Bean
    public TelegramClient createClient(
            @Value("${tg.token.main}") String botToken,
            @Value("${tg.proxy:}") String proxyConfig
    ) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // Настройка прокси, если указан
        if (proxyConfig != null && !proxyConfig.isEmpty()) {
            Proxy proxy = parseProxy(proxyConfig);
            if (proxy != null) {
                httpClientBuilder.proxy(proxy);

                // Если есть логин:пароль, добавляем аутентификацию
                String auth = parseProxyAuth(proxyConfig);
                if (auth != null) {
                    String[] parts = auth.split(":");
                    String username = parts[0];
                    String password = parts[1];

                    Authenticator proxyAuthenticator = (route, response) -> {
                        String credential = Credentials.basic(username, password);
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credential)
                                .build();
                    };
                    httpClientBuilder.proxyAuthenticator(proxyAuthenticator);
                }
            }
        }

        return new OkHttpTelegramClient(httpClientBuilder.build(), botToken);
    }

    /**
     * Парсит прокси из строки формата: логин:пароль@айпи:порт
     * Пример: user:pass@192.168.1.1:1080
     */
    private Proxy parseProxy(String proxyConfig) {
        try {
            String hostPort;
            String host;
            int port;

            // Извлекаем host:port после @ если есть
            if (proxyConfig.contains("@")) {
                hostPort = proxyConfig.split("@")[1];
            } else {
                hostPort = proxyConfig;
            }

            // Разделяем host и port
            String[] parts = hostPort.split(":");
            if (parts.length == 2) {
                host = parts[0];
                port = Integer.parseInt(parts[1]);
            } else {
                log.warn("Invalid proxy format: {}", proxyConfig);
                return null;
            }

            // По умолчанию SOCKS5 (быстрее)
            Proxy.Type type = Proxy.Type.HTTP;
            return new Proxy(type, new InetSocketAddress(host, port));

        } catch (Exception e) {
            log.error("Failed to parse proxy config: {}", proxyConfig, e);
            return null;
        }
    }

    /**
     * Извлекает логин:пароль из строки прокси
     */
    private String parseProxyAuth(String proxyConfig) {
        if (proxyConfig.contains("@")) {
            return proxyConfig.split("@")[0];
        }
        return null;
    }
}
