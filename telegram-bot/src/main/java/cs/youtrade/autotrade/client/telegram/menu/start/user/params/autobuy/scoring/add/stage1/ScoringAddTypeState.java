package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
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
            ScoringAddRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
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
        var translations = botTexts.forUser(userData, "parameters.scoring.UserScoring");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote>👤 %s
                        👥 %s
                        📏 %s</blockquote>
                        """,
                        DynamicEmoji.CHOOSE.getEmoji(),
                        translations.get("choose-a-scoring-type"),
                        translations.format("single-b-data-for", Map.of("value1", String.format("<b>%s</b>", translations.get("the-current-item-only")), "emphasis", String.format("<b>%s</b>", translations.get("single-b-data-for.emphasis")))),
                        translations.format("group-b-data-for", Map.of("value1", String.format("<b>%s</b>", translations.get("all-wear-conditions-of-the-item")), "emphasis", String.format("<b>%s</b>", translations.get("group-b-data-for.emphasis")))),
                        translations.format("averaged-b-averaged", Map.of("value1", String.format("<b>%s</b>", translations.get("item-data-over-time")), "emphasis", String.format("<b>%s</b>", translations.get("averaged-b-averaged.emphasis")))));
    }
}
