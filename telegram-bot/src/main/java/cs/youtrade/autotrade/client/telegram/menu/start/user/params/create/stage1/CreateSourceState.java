package cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.ParamsCreateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.ParamsCreateRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.prototype.AbstractCreateState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class CreateSourceState extends AbstractCreateState {
    public CreateSourceState(
            UserTextMessageSender sender,
            ParamsCreateRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, registry, botTexts);
    }

    @Override
    public boolean visibilityCondition(MarketType type) {
        return type.isAutobuy();
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_CREATE_STAGE_1;
    }

    @Override
    public MarketType getOption(String optionStr) {
        return MarketType.valueOf(optionStr);
    }

    @Override
    public MarketType[] getOptions(UserData userData) {
        return MarketType.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, MarketType t) {
        if (t.equals(MarketType.RETURN))
            return UserMenu.PARAMS;

        var data = registry.getOrCreate(user, ParamsCreateData::new);
        data.setSource(t);
        return UserMenu.PARAMS_CREATE_STAGE_2;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.ParamsCreate");
        return String.format("""
                        %s <b>%s</b>
                        └ <i>%s</i>
                        """,
                        DynamicEmoji.ITEM_RECEIVE.getEmoji(),
                        translations.get("buying-source"),
                        translations.get("choose-a-marketplace-for-automatic-buying"));
    }
}
