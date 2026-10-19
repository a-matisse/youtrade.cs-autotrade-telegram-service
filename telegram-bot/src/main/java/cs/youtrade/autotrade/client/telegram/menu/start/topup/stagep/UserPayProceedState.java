package cs.youtrade.autotrade.client.telegram.menu.start.topup.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.prototype.UserInitializer;
import cs.youtrade.autotrade.client.telegram.prototype.UserRegistry;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdTopUpDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.telegram.buttons.menu.InlineKeyboardButtonStyle;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class UserPayProceedState extends YTPTextMenuState<UserPayProceedMenu> {
    private static final Map<UserData, FcdTopUpDto> subMap = new ConcurrentHashMap<>();

    private final UserPayRegistry registry;
    private final SubGetEndpoint endpoint;
    private final UserRegistry userRegistry;
    private final UserInitializer userInitializer;

    public UserPayProceedState(
            UserTextMessageSender sender,
            UserPayRegistry registry,
            SubGetEndpoint endpoint,
            BotTexts botTexts,
            UserRegistry userRegistry,
            UserInitializer userInitializer
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
        this.userRegistry = userRegistry;
        this.userInitializer = userInitializer;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOP_UP_STAGE_P;
    }

    @Override
    public UserPayProceedMenu getOption(String optionStr) {
        return UserPayProceedMenu.valueOf(optionStr);
    }

    @Override
    public UserPayProceedMenu[] getOptions(UserData userData) {
        return UserPayProceedMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserPayProceedMenu t) {
        return switch (t) {
            case PAY, RETURN -> UserMenu.START;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "topup.UserPayProceed");
        var data = registry.remove(user);
        var restAns = endpoint.topUp(user.getChatId(), data.getAmount());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        subMap.put(user, fcd);
        return String.format("""
                    %s <b>%s</b>
                    
                    • %s: <b><code>%d</code></b>
                    • %s: <b>$%.2f</b> <i>(<b>≈ %.2f ₽</b>)</i>
                    • %s: <b>%s</b>
                    
                    <blockquote>%s <b>%s</b>
                    • %s: <b>HeleketPay</b>
                    • %s
                    
                    %s <i>%s</i>
                    %s <i>%s</i></blockquote>
                    """,
                    DynamicEmoji.YOUTRADE.getEmoji(),
                    translations.get("balance-top-up"),
                    translations.get("user-id"),
                    fcd.getUserTdId(),
                    translations.get("amount"),
                    fcd.getUsdAmount(),
                    fcd.getRubAmount(),
                    translations.get("operator"),
                    fcd.getType(),
                    DynamicEmoji.SECURE.getEmoji(),
                    translations.get("secure-payment"),
                    translations.get("payment-partner"),
                    translations.get("standard-aml-check"),
                    DynamicEmoji.WAIT.getEmoji(),
                    translations.get("the-link-is-valid-for-60-minutes"),
                    DynamicEmoji.FAST.getEmoji(),
                    translations.get("your-balance-is-credited-automatically"));
    }

    @Override
    public Map<UserPayProceedMenu, String> getUrls(UserData user) {
        var fcd = subMap.get(user);
        return Map.of(
                UserPayProceedMenu.PAY, fcd.getUrl()
        );
    }

    @Override
    public Map<UserPayProceedMenu, Function<UserData, InlineKeyboardButtonStyle>> getButtonStyle(UserData userData) {
        return Map.of(
                UserPayProceedMenu.PAY, u -> InlineKeyboardButtonStyle.PRIMARY
        );
    }

    @Override
    public void executeSide(TelegramClient bot, Update update, UserData userData) {
        FcdTopUpDto ans = subMap.remove(userData);
        ans.getAdminChats().forEach(adminChatId -> {
            var admin = userRegistry.getOrCreateUser(adminChatId, userInitializer::initUser);
            String notification = getNotification(update, userData, ans, botTexts.forUser(admin, "topup.UserPayProceed"));
            sender.sendTextMes(bot, admin, notification);
        });
    }

    private String getNotification(Update update, UserData userData, FcdTopUpDto ans, LocalizedTexts translations) {
        long tdId = ans.getUserTdId();
        long chatId = userData.getChatId();
        String username = String.format("[@%s]", update.getCallbackQuery().getFrom().getUserName());

        return translations.format("admin-top-up-request", Map.of("username", username,
                "userId", String.valueOf(tdId), "usd", ans.getUsdAmount().toPlainString(),
                "rub", ans.getRubAmount().toPlainString(), "chatId", String.valueOf(chatId)));
    }
}
