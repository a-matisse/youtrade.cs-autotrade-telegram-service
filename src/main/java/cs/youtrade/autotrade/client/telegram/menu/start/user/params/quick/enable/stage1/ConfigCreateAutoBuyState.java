package cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.QuickConfigCreateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.QuickConfigCreateRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.prototype.AbstractQuickConfigGradeState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.prototype.QuickConfigGradeMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ConfigCreateAutoBuyState extends AbstractQuickConfigGradeState {
    private final QuickConfigCreateRegistry registry;

    public ConfigCreateAutoBuyState(
            UserTextMessageSender sender,
            QuickConfigCreateRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_QUICK_CONFIG_INIT_STAGE_1;
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, QuickConfigGradeMenu t) {
        if (t.equals(QuickConfigGradeMenu.RETURN))
            return UserMenu.PARAMS;
        var data = registry.getOrCreate(user, QuickConfigCreateData::new);
        var grade = t.getGrade();
        data.setBuyGrade(grade);
        return UserMenu.PARAMS_QUICK_CONFIG_INIT_STAGE_2;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return """
                🎚 <b>Строгость покупки</b>
                ━━━━━━━━━━━━━━━━━━━━━
                
                Выберите уровень фильтрации предметов.
                Более строгий режим снижает количество покупок,
                но повышает их качество.
                """;
    }
}
