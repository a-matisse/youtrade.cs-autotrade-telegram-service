package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.stage1;

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
public class ConfigCreateAutoBuyState extends AbstractQuickConfigGradeState {
    private final QuickConfigCreateRegistry registry;

    public ConfigCreateAutoBuyState(
            UserTextMessageSender sender,
            QuickConfigCreateRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.USER_QUICK_CONFIG_INIT_STAGE_1;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, QuickConfigGradeMenu t) {
        if (t.equals(QuickConfigGradeMenu.RETURN))
            return UserMenu.USER;
        var data = registry.getOrCreate(user, QuickConfigCreateData::new);
        var grade = t.getGrade();
        data.setBuyGrade(grade);
        return UserMenu.USER_QUICK_CONFIG_INIT_STAGE_2;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.QuickConfig");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote><i>%s</i></blockquote>
                        """,
                        DynamicEmoji.ITEM_RECEIVE.getEmoji(),
                        translations.get("choose-the-buying-strictness-level"),
                        translations.format("a-mode-purchases-but-their", Map.of("value1", String.format("<b>%s</b>", translations.get("stricter-2")), "value2", String.format("<b>%s</b>", translations.get("reduces-the-number-of")), "value3", String.format("<b>%s</b>", translations.get("improves")), "value4", String.format("<b>%s</b>", translations.get("quality")))));
    }
}
