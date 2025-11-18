package cs.youtrade.autotrade.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@Log4j2
public class Main {
    private static final SpringApplication app = new SpringApplication(Main.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = app.run();

        log.debug("STARTED APPLICATION WITH BEANS: ");
        for (String beanName : applicationContext.getBeanDefinitionNames())
            log.debug("--{}", beanName);
    }
}