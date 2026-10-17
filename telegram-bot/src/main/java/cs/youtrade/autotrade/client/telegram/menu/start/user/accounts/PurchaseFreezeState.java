package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class PurchaseFreezeState extends YTPTerminalTextMenuState {
    private final PurchaseFreezeRegistry registry;
    private final YTPPageProcessor pageProcessor;

    public PurchaseFreezeState(UserTextMessageSender sender, PurchaseFreezeRegistry registry,
                               YTPPageProcessor pageProcessor) {
        super(sender);
        this.registry = registry;
        this.pageProcessor = pageProcessor;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_PURCHASE_FREEZE; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        try {
            return String.format("""
                    %s <i>Пауза покупок</i>

                    %s <b>Отправьте ID аккаунта покупки</b>
                    <blockquote expandable>%s</blockquote>
                    """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.WRITE.getEmoji(),
                    pageProcessor.getPage(user.getChatId()).getAccountsListStr());
        } catch (RuntimeException error) {
            return String.format("""
                    %s <i>Пауза покупок</i>

                    Не удалось загрузить аккаунты. Попробуйте снова.
                    """, DynamicEmoji.YOUTRADE.getEmoji());
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        long id;
        try {
            id = Long.parseLong(update.getMessage().getText().trim());
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
            return supportedState();
        }
        try {
            var account = pageProcessor.getPage(user.getChatId()).fcd().getAccounts().stream()
                    .filter(item -> item.getId().equals(id)).findFirst().orElse(null);
            if (account == null || account.getBuyBalance() == null) {
                sender.sendTextMes(bot, user, "Аккаунт покупки не найден на текущей странице.");
                return supportedState();
            }
            var data = registry.getOrCreate(user, PurchaseFreezeData::new);
            data.setTokenId(id);
            data.setAccount(account);
            return UserMenu.ACCOUNTS_PURCHASE_FREEZE_PERIOD;
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
            return supportedState();
        }
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS_MORE;
    }
}
