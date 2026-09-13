package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.properties.PropertiesEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Service
public class UserPreferencesState extends YTPTextMenuState<UserPreferencesMenu> {
    private final ParamsEndpoint paramsEndpoint;
    private final PropertiesEndpoint propertiesEndpoint;
    private final Map<Long, Boolean> notifications = new ConcurrentHashMap<>();

    public UserPreferencesState(UserTextMessageSender sender, ParamsEndpoint paramsEndpoint,
                                PropertiesEndpoint propertiesEndpoint) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
        this.propertiesEndpoint = propertiesEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PREFERENCES;
    }

    @Override
    public UserPreferencesMenu getOption(String optionStr) {
        return UserPreferencesMenu.valueOf(optionStr);
    }

    @Override
    public UserPreferencesMenu[] getOptions(UserData user) {
        return UserPreferencesMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, UserPreferencesMenu option) {
        return switch (option) {
            case BARGAIN_NOTIFICATIONS_ON, BARGAIN_NOTIFICATIONS_OFF -> {
                var ans = propertiesEndpoint.toggleBargainNotifications(user.getChatId());
                if (ans.getStatus() < 300 && !ans.getResponse().isResult())
                    sender.sendTextMes(bot, user, ans.getResponse().getCause());
                yield UserMenu.PREFERENCES;
            }
            case RETURN -> UserMenu.PARAMS;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var restAns = paramsEndpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;
        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        boolean full = Boolean.TRUE.equals(fcd.getData().getBargainNotifications());
        notifications.put(user.getChatId(), full);
        return String.format("""
                %s <i>Настройки удобства</i>

                %s

                %s <b>Личные настройки</b>
                <blockquote>%s Сообщения торгов: <b>%s</b></blockquote>
                """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                fcd.getData().getProfileStr(user),
                DynamicEmoji.SETTINGS.getEmoji(),
                (full ? DynamicEmoji.ON : DynamicEmoji.OFF).getEmoji(),
                full ? "Полные" : "Короткие");
    }

    @Override
    public Map<UserPreferencesMenu, Predicate<UserData>> getVisibilityPredicates(UserData user) {
        return Map.of(
                UserPreferencesMenu.BARGAIN_NOTIFICATIONS_ON, u -> Boolean.TRUE.equals(notifications.get(u.getChatId())),
                UserPreferencesMenu.BARGAIN_NOTIFICATIONS_OFF, u -> Boolean.FALSE.equals(notifications.get(u.getChatId())));
    }
}
