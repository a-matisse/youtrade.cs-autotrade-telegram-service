package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.empty;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableEmptyReason;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableType;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class PortfolioEmptyState extends YTPTerminalTextMenuState {
    private final PortfolioTableRegistry registry;

    public PortfolioEmptyState(UserTextMessageSender sender, PortfolioTableRegistry registry, BotTexts botTexts) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_EMPTY;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "portfolio.PortfolioEmpty");
        var data = registry.getOrCreate(user, PortfolioTableData::new);
        var type = data.getType();
        if (type == null) return null;
        if (data.getEmptyReason() == PortfolioTableEmptyReason.NO_SELL_ACCOUNTS)
            return String.format("""
                    %s <b>%s</b>

                    %s <i>%s</i>
                    └ <i>%s</i>
                    """,
                    DynamicEmoji.EXCEL.getEmoji(),
                    title(type, translations),
                    DynamicEmoji.SLEEP.getEmoji(),
                    translations.get("no-selling-accounts-added"),
                    translations.get("add-a-selling-account-in-accounts"));
        return String.format("""
                %s <b>%s</b>

                %s <i>%s</i>
                """, DynamicEmoji.EXCEL.getEmoji(), title(type, translations), DynamicEmoji.SLEEP.getEmoji(), description(type, translations));
    }

    private String title(PortfolioTableType type, LocalizedTexts translations) {
        return switch (type) {
            case INVENTORY -> translations.get("your-inventory");
            case SELLING -> translations.get("your-listings");
            case WAITING -> translations.get("pending-items");
            case BUY_HISTORY -> translations.get("purchase-history");
            case SELL_HISTORY -> translations.get("sales-history");
        };
    }

    private String description(PortfolioTableType type, LocalizedTexts translations) {
        return switch (type) {
            case INVENTORY -> translations.get("your-inventory-is-empty");
            case SELLING -> translations.get("no-items-listed-yet");
            case WAITING -> translations.get("no-pending-items-yet");
            case BUY_HISTORY -> translations.get("no-purchases-for-the-selected-period");
            case SELL_HISTORY -> translations.get("no-sales-for-the-selected-period");
        };
    }
}
