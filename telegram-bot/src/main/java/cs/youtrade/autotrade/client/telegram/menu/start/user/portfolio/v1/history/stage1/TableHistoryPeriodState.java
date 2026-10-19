package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableLoader;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryMode;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Log4j2
public class TableHistoryPeriodState extends YTPTextMenuState<TableHistoryPeriodMenu> {
    private final TableHistoryRegistry registry;
    private final PortfolioTableLoader tableLoader;

    public TableHistoryPeriodState(
            UserTextMessageSender sender,
            TableHistoryRegistry registry,
            PortfolioTableLoader tableLoader,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.tableLoader = tableLoader;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.history.TableHistoryPeriod");
        var data = registry.getOrCreate(userData, TableHistoryData::new);
        boolean sellHistory = data.getMode() == TableHistoryMode.SELL;
        String title = sellHistory ? translations.get("sales-history") : translations.get("purchase-history");
        String description = sellHistory
                ? String.format("%s",
                        translations.format("items-sold-b-during-the-selected-period", Map.of("emphasis", String.format("<b>%s</b>", translations.get("items-sold-b-during-the-selected-period.emphasis")))))
                : String.format("%s",
                        translations.format("items-bought-b-during-the-selected-period", Map.of("emphasis", String.format("<b>%s</b>", translations.get("items-bought-b-during-the-selected-period.emphasis")))));
        String result = sellHistory
                ? translations.format("totals-include-volume-earnings-and", Map.of("value1", String.format("<b>%s</b>", translations.get("actual-return"))))
                : translations.format("totals-include-quantity-and", Map.of("value1", String.format("<b>%s</b>", translations.get("total-purchase-volume"))));
        String directionEmoji = sellHistory
                ? DynamicEmoji.ITEM_SEND.getEmoji()
                : DynamicEmoji.ITEM_RECEIVE.getEmoji();

        return String.format("""
                        %s <b>%s</b>

                        <blockquote>%s %s
                        %s %s</blockquote>

                        %s <b>%s</b>
                        """,
                        DynamicEmoji.EXCEL.getEmoji(),
                        title,
                        directionEmoji,
                        description,
                        DynamicEmoji.GRAPH.getEmoji(),
                        result,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("choose-a-period-below-or-send-your"));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_1;
    }

    @Override
    public TableHistoryPeriodMenu getOption(String optionStr) {
        return TableHistoryPeriodMenu.valueOf(optionStr);
    }

    @Override
    public TableHistoryPeriodMenu[] getOptions(UserData userData) {
        return TableHistoryPeriodMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TableHistoryPeriodMenu option) {
        return prepare(bot, user, option.getDays());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "portfolio.history.TableHistoryPeriod");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.PORTFOLIO;
        }

        String input = update.getMessage().getText();
        int days;
        try {
            days = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                    translations.get("the-value-entered-is-not-a-positive"),
                    input));
            return UserMenu.PORTFOLIO;
        }

        return prepare(bot, user, days);
    }

    private UserMenu prepare(TelegramClient bot, UserData user, int days) {
        var translations = botTexts.forUser(user, "portfolio.history.TableHistoryPeriod");
        var data = registry.getOrCreate(user, TableHistoryData::new);
        data.setPeriod(days);
        try {
            return switch (data.getMode()) {
                case SELL -> tableLoader.sellHistory(user, days);
                case BUY -> tableLoader.buyHistory(user, days);
                case RETURN -> UserMenu.PORTFOLIO;
            };
        } catch (RuntimeException error) {
            log.warn("Couldn't load portfolio history for chat {}", user.getChatId(), error);
            sender.sendTextMes(bot, user, ErrorMessageGenerator.getErrorText(translations.inBundle("common.Interface")));
            return supportedState();
        }
    }
}
