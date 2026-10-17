package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage7;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.AbstractBalanceMarketSellState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellText;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Log4j2
public class BalanceMarketSellProcessingState extends AbstractBalanceMarketSellState<BalanceMarketSellProcessingMenu> {
    public BalanceMarketSellProcessingState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                            BalanceMarketSellText text) {
        super(sender, registry, text);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL_PROCESSING; }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, BalanceMarketSellProcessingMenu option) {
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        return data.getResult() == null ? supportedState() : UserMenu.ACCOUNTS_SELL_RESULT;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Создаём заявку...</b>
                """, text.summary(registry.getOrCreate(user, BalanceMarketSellData::new)), DynamicEmoji.WAIT.getEmoji());
    }

    public void show(TelegramClient bot, UserData user) {
        try {
            bot.execute(buildEdit(bot, user));
        } catch (TelegramApiException | RuntimeException error) {
            log.warn("Couldn't show sale request progress: {}", error.getMessage());
        }
    }

    @Override
    public BalanceMarketSellProcessingMenu getOption(String value) {
        return BalanceMarketSellProcessingMenu.valueOf(value);
    }

    @Override
    public BalanceMarketSellProcessingMenu[] getOptions(UserData user) {
        return BalanceMarketSellProcessingMenu.values();
    }
}
