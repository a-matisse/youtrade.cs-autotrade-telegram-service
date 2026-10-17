package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage1;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoAccountLookup;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.AbstractBalanceMarketSellTerminalState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellText;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class BalanceMarketSellState extends AbstractBalanceMarketSellTerminalState {
    private final MarketCsgoAccountLookup lookup;

    public BalanceMarketSellState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                  BalanceMarketSellText text, MarketCsgoAccountLookup lookup) {
        super(sender, registry, text);
        this.lookup = lookup;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        String summary = text.summary(registry.getOrCreate(user, BalanceMarketSellData::new));
        try {
            if (!lookup.isMarketCsgo(user.getChatId()))
                return String.format("""
                        %sПродажа баланса доступна только для направления Market.CSGO.
                        """, summary);
            return String.format("""
                    %s%s <b>Отправьте ID аккаунта, с которого хотите продать баланс</b>
                    <blockquote expandable>%s</blockquote>
                    """, summary, DynamicEmoji.WRITE.getEmoji(), lookup.currentPageBalances(user.getChatId()));
        } catch (RuntimeException error) {
            return String.format("""
                        %sНе удалось загрузить аккаунты. Попробуйте снова.
                        """, summary);
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!hasText(bot, update, user)) return supportedState();
        try {
            if (!lookup.isMarketCsgo(user.getChatId())) {
                sender.sendTextMes(bot, user, "Продажа баланса доступна только для направления Market.CSGO.");
                return supportedState();
            }
            long id = Long.parseLong(update.getMessage().getText().trim());
            var account = lookup.find(user.getChatId(), id);
            if (account == null || account.available() == null) {
                sender.sendTextMes(bot, user, "Аккаунт с продажей Market.CSGO не найден в текущих параметрах.");
                return supportedState();
            }
            if (account.available().signum() <= 0) {
                sender.sendTextMes(bot, user, "С этого аккаунта нечего продавать: доступный баланс равен нулю.");
                return supportedState();
            }
            var data = registry.getOrCreate(user, BalanceMarketSellData::new);
            data.setAccountId(id);
            data.setAvailable(account.available());
            return UserMenu.ACCOUNTS_SELL_AMOUNT;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
        }
        return supportedState();
    }
}
