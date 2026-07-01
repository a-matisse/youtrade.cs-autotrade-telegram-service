package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.ItemScoringTypeMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.ScoringAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.ScoringAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ScoringAddTypeState extends YTPTextMenuState<ItemScoringTypeMenu> {
    private final ScoringAddRegistry registry;

    public ScoringAddTypeState(
            UserTextMessageSender sender,
            ScoringAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_ADD_STAGE_1;
    }

    @Override
    public ItemScoringTypeMenu getOption(String optionStr) {
        return ItemScoringTypeMenu.valueOf(optionStr);
    }

    @Override
    public ItemScoringTypeMenu[] getOptions(UserData userData) {
        return ItemScoringTypeMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, ItemScoringTypeMenu t) {
        if (t.equals(ItemScoringTypeMenu.RETURN))
            return UserMenu.SCORING;

        var data = registry.getOrCreate(user, ScoringAddData::new);
        data.setType(t.getItemScoringType());
        return UserMenu.SCORING_ADD_STAGE_2;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Выберите тип скоринга</b>
                        
                        <blockquote>👤 <b>Одиночный</b> — данные только по <b>текущему предмету</b>
                        👥 <b>Групповой</b> — данные <b>всех износов предмета</b>
                        📏 <b>Усредненная</b> — усредненные <b>временные данные предмета</b></blockquote>
                        """,
                DynamicEmoji.CHOOSE.getEmoji()
        );
    }
}
