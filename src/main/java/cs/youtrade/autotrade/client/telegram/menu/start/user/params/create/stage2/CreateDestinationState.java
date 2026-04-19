package cs.youtrade.autotrade.client.telegram.menu.start.user.params.create.stage2;

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
public class CreateDestinationState extends AbstractCreateState {
    public CreateDestinationState(
            UserTextMessageSender sender,
            ParamsCreateRegistry registry
    ) {
        super(sender, registry);
    }

    @Override
    public boolean visibilityCondition(MarketType type) {
        return type.isAutosell();
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS_CREATE_STAGE_2;
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
        var data = registry.getOrCreate(user, ParamsCreateData::new);
        data.setDestination(t);
        return UserMenu.PARAMS_CREATE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Направление продажи</b>
                        └ <i>Выберите площадку для автоматической продажи</i>
                        """,
                DynamicEmoji.ITEM_SEND.getEmoji()
        );
    }
}
