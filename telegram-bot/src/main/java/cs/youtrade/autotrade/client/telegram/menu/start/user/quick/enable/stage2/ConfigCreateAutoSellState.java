package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.QuickConfigCreateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.QuickConfigCreateRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.prototype.AbstractQuickConfigGradeState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.prototype.QuickConfigGradeMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ConfigCreateAutoSellState extends AbstractQuickConfigGradeState {
    private final QuickConfigCreateRegistry registry;

    public ConfigCreateAutoSellState(
            UserTextMessageSender sender,
            QuickConfigCreateRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.USER_QUICK_CONFIG_INIT_STAGE_2;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, QuickConfigGradeMenu t) {
        if (t.equals(QuickConfigGradeMenu.RETURN))
            return UserMenu.USER;
        var data = registry.getOrCreate(user, QuickConfigCreateData::new);
        var grade = t.getGrade();
        data.setSellGrade(grade);
        return UserMenu.USER_QUICK_CONFIG_INIT_STAGE_3;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.QuickConfig");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote>%s</blockquote>
                        """,
                        DynamicEmoji.ITEM_SEND.getEmoji(),
                        translations.get("choose-the-selling-strictness-level"),
                        translations.format("a-mode-trades-but", Map.of("value1", String.format("<b>%s</b>", translations.get("stricter")), "value2", String.format("<b>%s</b>", translations.get("reduces-the-frequency-of")), "value3", String.format("<b>%s</b>", translations.get("increases-potential-profit")))));
    }
}
