package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.UserAutoSellUpdateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update.UserAutoSellUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.TdpField;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoSellUpdateFieldState extends YTPTextState {
    private final UserAutoSellUpdateRegistry registry;

    public AutoSellUpdateFieldState(
            UserTextMessageSender sender,
            UserAutoSellUpdateRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Введите поле, которое хотите изменить...</b>
                        
                        <blockquote>%s</blockquote>
                        """,
                DynamicEmoji.WRITE.getEmoji(), TdpField.generateDescription(TdpField.DirType.SELL)
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.AUTOSELL;
        }

        String field = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoSellUpdateData::new);
        TdpField tdpF = TdpField.fromFName(field);
        if (tdpF == null) {
            sender.sendTextMes(bot, user, "#0: Поле не найдено. Возвращение обратно...");
            return UserMenu.SCORING;
        }

        data.setField(tdpF);
        return UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_2;
    }
}
