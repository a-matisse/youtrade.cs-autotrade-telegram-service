package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.empty;

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

    public PortfolioEmptyState(UserTextMessageSender sender, PortfolioTableRegistry registry) {
        super(sender);
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
        var data = registry.getOrCreate(user, PortfolioTableData::new);
        var type = data.getType();
        if (type == null) return null;
        if (data.getEmptyReason() == PortfolioTableEmptyReason.NO_SELL_ACCOUNTS)
            return String.format("""
                    %s <b>%s</b>

                    %s <i>Аккаунты продажи не добавлены</i>
                    └ <i>Добавьте аккаунт продажи в /accounts</i>
                    """, DynamicEmoji.EXCEL.getEmoji(), title(type), DynamicEmoji.SLEEP.getEmoji());
        return String.format("""
                %s <b>%s</b>

                %s <i>%s</i>
                """, DynamicEmoji.EXCEL.getEmoji(), title(type), DynamicEmoji.SLEEP.getEmoji(), description(type));
    }

    private String title(PortfolioTableType type) {
        return switch (type) {
            case INVENTORY -> "Инвентарь пользователя";
            case SELLING -> "Витрина пользователя";
            case WAITING -> "Список ожидаемых предметов";
            case BUY_HISTORY -> "История покупок";
            case SELL_HISTORY -> "История продаж";
        };
    }

    private String description(PortfolioTableType type) {
        return switch (type) {
            case INVENTORY -> "Инвентарь пока пуст";
            case SELLING -> "На витрине пока нет предметов";
            case WAITING -> "В ожидании пока нет предметов";
            case BUY_HISTORY -> "Покупок за выбранный период нет";
            case SELL_HISTORY -> "Продаж за выбранный период нет";
        };
    }
}
