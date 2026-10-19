package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.update.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.update.UserAutoBuyUpdateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.update.UserAutoBuyUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoBuyUpdateValueState extends YTPTextState {
    private final UserAutoBuyUpdateRegistry registry;

    public AutoBuyUpdateValueState(
            UserTextMessageSender sender,
            UserAutoBuyUpdateRegistry registry,
            BotTexts botTexts
    ) {

        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.autobuy.AutoBuyUpdate");
        var data = registry.getOrCreate(userData, UserAutoBuyUpdateData::new);
        return String.format("""
                        %s <b>%s</b>
                        └ %s
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("enter-a-value-for-the-selected-field"),
                        data.getField().getForkByField(translations.inBundle("parameters.TdpField")));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.AutoBuyUpdate");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.AUTOBUY;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoBuyUpdateData::new);
        data.setValue(value);
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_P;
    }
}
