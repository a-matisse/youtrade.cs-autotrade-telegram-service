package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.choose;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryMode;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableHistoryModeState extends YTPTextMenuState<TableHistoryMode> {
    private final TableHistoryRegistry registry;

    public TableHistoryModeState(
            UserTextMessageSender sender,
            TableHistoryRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_CHOOSE;
    }

    @Override
    public TableHistoryMode getOption(String optionStr) {
        return TableHistoryMode.valueOf(optionStr);
    }

    @Override
    public TableHistoryMode[] getOptions(UserData userData) {
        return TableHistoryMode.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TableHistoryMode t) {
        if (t.equals(TableHistoryMode.RETURN))
            return UserMenu.PORTFOLIO;

        var data = registry.getOrCreate(user, TableHistoryData::new);
        data.setMode(t);
        return UserMenu.PORTFOLIO_HISTORY_STAGE_1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.history.TableHistoryPeriod");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote>%s %s
                        %s %s</blockquote>
                        """,
                        DynamicEmoji.CHOOSE.getEmoji(),
                        translations.get("choose-a-trade-history-type"),
                        DynamicEmoji.ITEM_RECEIVE.getEmoji(),
                        translations.format("buying-b-purchases", Map.of("value1", String.format("<b>%s</b>", translations.get("without-profit-statistics")), "emphasis", String.format("<b>%s</b>", translations.get("buying-b-purchases.emphasis")))),
                        DynamicEmoji.ITEM_SEND.getEmoji(),
                        translations.format("selling-b-sales", Map.of("value1", String.format("<b>%s</b>", translations.get("with-profit-statistics")), "emphasis", String.format("<b>%s</b>", translations.get("selling-b-sales.emphasis")))));
    }
}
