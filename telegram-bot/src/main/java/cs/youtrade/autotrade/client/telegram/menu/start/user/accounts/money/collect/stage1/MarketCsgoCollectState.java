package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stage1;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.MarketCsgoAccountLookup;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoCollectState extends YTPTerminalTextMenuState {
    private final MarketCsgoAccountLookup lookup;
    private final MarketCsgoCollectRegistry registry;

    public MarketCsgoCollectState(UserTextMessageSender sender, MarketCsgoAccountLookup lookup,
                                  MarketCsgoCollectRegistry registry) {
        super(sender);
        this.lookup = lookup;
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_COLLECT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        try {
            if (!lookup.isMarketCsgo(user.getChatId()))
                return "Перевод баланса доступен только для направления Market.CSGO.";
            return String.format("""
                    %s <i>Сбор баланса Market.CSGO</i>

                    %s <b>Получатель</b>
                    <blockquote>• Аккаунт: —
                    • Пароль: —</blockquote>

                    %s <b>Отправьте ID аккаунта, на который собрать балансы</b>
                    <blockquote expandable>%s</blockquote>
                    """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                    DynamicEmoji.WRITE.getEmoji(), lookup.currentPageBalances(user.getChatId()));
        } catch (RuntimeException error) {
            return "Не удалось загрузить аккаунты. Попробуйте снова.";
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
                sender.sendTextMes(bot, user, "Перевод баланса доступен только для направления Market.CSGO.");
                return supportedState();
            }
            long id = Long.parseLong(update.getMessage().getText().trim());
            var account = lookup.find(user.getChatId(), id);
            if (account == null || account.available() == null) {
                sender.sendTextMes(bot, user, "Аккаунт с продажей Market.CSGO не найден в текущих параметрах.");
                return supportedState();
            }
            registry.getOrCreate(user, MarketCsgoCollectData::new).setTokenId(id);
            return UserMenu.ACCOUNTS_COLLECT_PASSWORD;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
        }
        return supportedState();
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }
}
