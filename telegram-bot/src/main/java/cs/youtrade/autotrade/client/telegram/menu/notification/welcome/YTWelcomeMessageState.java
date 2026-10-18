package cs.youtrade.autotrade.client.telegram.menu.notification.welcome;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTNotificationMenu;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.YTNotificationType;
import cs.youtrade.autotrade.client.util.notification.YTUserCreatedNotification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

@Service
public class YTWelcomeMessageState extends YTNotificationMenu<YTWelcomeMessageMenu, YTUserCreatedNotification> {
    private static final String DOCUMENTATION_LINK = "https://docs.youtradecs.xyz";
    private final RedisTemplate<String, String> redisTemplate;

    public YTWelcomeMessageState(UserTextMessageSender sender, RedisTemplate<String, String> redisTemplate) {
        super(sender);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.NOTIFICATION_WELCOME;
    }

    @Override
    public YTNotificationType getNotificationType() {
        return YTNotificationType.USER_CREATED;
    }

    @Override
    public boolean isReplacing() {
        return false;
    }

    @Override
    public void executeOnState(TelegramClient bot, UserData user, UserStateData lastState, Object data) {
        var notification = (YTUserCreatedNotification) data;
        if (notification.getTdId() == null)
            throw new IllegalArgumentException("Welcome notification has no tdId");
        String key = "telegram:welcome-message:" + notification.getTdId();
        if (!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "sent"))) return;
        try {
            super.executeOnState(bot, user, lastState, data);
        } catch (RuntimeException error) {
            redisTemplate.delete(key);
            throw error;
        }
    }

    @Override
    public YTWelcomeMessageMenu getOption(String optionStr) {
        return YTWelcomeMessageMenu.valueOf(optionStr.substring(optionStr.indexOf(':') + 1));
    }

    @Override
    public YTWelcomeMessageMenu[] getOptions(UserData user) {
        return YTWelcomeMessageMenu.values();
    }

    @Override
    public Map<YTWelcomeMessageMenu, String> getUrls(UserData user) {
        return Map.of(YTWelcomeMessageMenu.TUTORIAL_URL, DOCUMENTATION_LINK);
    }

    @Override
    public SendMessage buildMessage(TelegramClient bot, UserData user, YTUserCreatedNotification data) {
        var message = super.buildMessage(bot, user, data);
        message.disableWebPagePreview();
        return message;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, YTWelcomeMessageMenu option) {
        return switch (option) {
            case LANGUAGE -> UserMenu.LANGUAGE;
            case TUTORIAL_URL -> getReturnMenu(user);
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user, YTUserCreatedNotification data) {
        return String.format("""
                %s <i>Сервис YouTrade.CS</i>

                %s <b>Добро пожаловать в Y.CS</b>
                <blockquote>Покупка и продажа предметов CS2 — автоматически.
                Подключите аккаунты и настройте торговлю под себя.</blockquote>

                %s <b>Первые шаги</b>
                <blockquote>В <a href="%s">туториале</a> — подключение аккаунтов, настройка и запуск торговли.</blockquote>

                %s <b>Language</b>
                └ <i>If needed, you can change your language below.</i>
                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.BLINK_SMILE.getEmoji(),
                DynamicEmoji.WONDER.getEmoji(), DOCUMENTATION_LINK, DynamicEmoji.GLOBE.getEmoji());
    }
}
