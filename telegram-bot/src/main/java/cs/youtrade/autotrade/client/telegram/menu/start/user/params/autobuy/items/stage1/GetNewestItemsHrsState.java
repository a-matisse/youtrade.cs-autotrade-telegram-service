package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.GetNewestItemsData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.GetNewestItemsRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class GetNewestItemsHrsState extends YTPTextMenuState<GetNewestItemsHrsMenu> {
    private final GetNewestItemsRegistry registry;

    public GetNewestItemsHrsState(
            UserTextMessageSender sender,
            GetNewestItemsRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_GET_NEWEST_ITEMS_STAGE_1;
    }

    @Override
    public GetNewestItemsHrsMenu getOption(String optionStr) {
        return GetNewestItemsHrsMenu.valueOf(optionStr);
    }

    @Override
    public GetNewestItemsHrsMenu[] getOptions(UserData userData) {
        return GetNewestItemsHrsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, GetNewestItemsHrsMenu option) {
        return prepare(user, option.getHours());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autobuy.GetNewestItems");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-returning-to-the-menu")));
            return UserMenu.AUTOBUY;
        }

        String input = update.getMessage().getText();
        int hrs;
        try {
            hrs = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                    translations.get("could-not-read-the-number"),
                    input));
            return UserMenu.AUTOBUY;
        }

        return prepare(user, hrs);
    }

    private UserMenu prepare(UserData user, int hrs) {
        var data = registry.getOrCreate(user, GetNewestItemsData::new);
        data.setHrs(hrs);
        return UserMenu.AUTOBUY_GET_NEWEST_ITEMS_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.autobuy.GetNewestItems");
        return String.format("""
                        %s <b>%s</b>

                        <blockquote>%s %s
                        %s %s</blockquote>

                        %s <b>%s</b>
                        """,
                        DynamicEmoji.BOX.getEmoji(),
                        translations.get("new-items"),
                        DynamicEmoji.EXCEL.getEmoji(),
                        translations.format("excel-spreadsheet-b-with-all-items-added", Map.of("emphasis", String.format("<b>%s</b>", translations.get("excel-spreadsheet-b-with-all-items-added.emphasis")))),
                        DynamicEmoji.CLOCK.getEmoji(),
                        translations.format("maximum-period", Map.of("value1", String.format("<b>24 %s</b>", translations.get("hours")))),
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("choose-a-period-below-or-send-your"));
    }
}
