package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage1;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoAccountLookup;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendTerminalState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendTransfer;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoSendState extends AbstractMarketCsgoSendTerminalState {
    private final MarketCsgoAccountLookup lookup;
    private final MarketCsgoSendTransfer transfer;

    public MarketCsgoSendState(UserTextMessageSender sender, MarketCsgoSendRegistry registry,
                               MarketCsgoAccountLookup lookup, MarketCsgoSendTransfer transfer) {
        super(sender, registry);
        this.lookup = lookup;
        this.transfer = transfer;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        try {
            if (!lookup.isMarketCsgo(user.getChatId()))
                return "Вывод доступен только для направления Market.CSGO.";
            return String.format("""
                    %s%s <b>Отправьте ID аккаунта, с которого хотите вывести деньги</b>
                    <blockquote expandable>%s</blockquote>
                    """, summary(user), DynamicEmoji.WRITE.getEmoji(), lookup.currentPageBalances(user.getChatId()));
        } catch (RuntimeException error) {
            return "Не удалось проверить направление торговли. Попробуйте ещё раз.";
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        try {
            if (!lookup.isMarketCsgo(user.getChatId())) {
                sender.sendTextMes(bot, user, "Вывод доступен только для направления Market.CSGO.");
                return supportedState();
            }
            long id = Long.parseLong(update.getMessage().getText().trim());
            var account = lookup.find(user.getChatId(), id);
            if (account == null || account.available() == null) {
                sender.sendTextMes(bot, user, "Аккаунт с продажей Market.CSGO не найден в текущих параметрах.");
                return supportedState();
            }
            if (account.available().signum() <= 0) {
                sender.sendTextMes(bot, user, "С этого аккаунта нечего переводить: доступный баланс равен нулю.");
                return supportedState();
            }
            if (account.available().compareTo(transfer.testAmount()) < 0) {
                sender.sendTextMes(bot, user, "Баланса недостаточно для пробного перевода $"
                        + transfer.testAmount().toPlainString() + ".");
                return supportedState();
            }
            var data = registry.getOrCreate(user, MarketCsgoSendData::new);
            data.setTokenId(id);
            data.setAvailable(account.available());
            return UserMenu.ACCOUNTS_SEND_PASSWORD;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
        }
        return supportedState();
    }
}
