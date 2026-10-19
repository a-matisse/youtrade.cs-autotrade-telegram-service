package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
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
                               YTPPageProcessor pageProcessor, BotTexts botTexts) {
        super(sender, botTexts);
        this.registry = registry;
        this.pageProcessor = pageProcessor;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_PURCHASE_FREEZE; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.PurchaseFreeze");
        try {
            return String.format("""
                    %s <i>%s</i>

                    %s <b>%s</b>
                    <blockquote expandable>%s</blockquote>
                    """,
                    DynamicEmoji.YOUTRADE.getEmoji(),
                    translations.get("purchase-pause"),
                    DynamicEmoji.WRITE.getEmoji(),
                    translations.get("send-the-buying-account-id"),
                    pageProcessor.getPage(user.getChatId()).getAccountsListStr(translations.inBundle("accounts.AccountOptions")));
        } catch (RuntimeException error) {
            return String.format("""
                    %s <i>%s</i>

                    %s
                    """,
                    DynamicEmoji.YOUTRADE.getEmoji(),
                    translations.get("purchase-pause"),
                    translations.get("could-not-load-accounts-try-again"));
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.PurchaseFreeze");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        long id;
        try {
            id = Long.parseLong(update.getMessage().getText().trim());
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, translations.get("enter-one-account-id-as-a-number"));
            return supportedState();
        }
        try {
            var account = pageProcessor.getPage(user.getChatId()).fcd().getAccounts().stream()
                    .filter(item -> item.getId().equals(id)).findFirst().orElse(null);
            if (account == null || account.getBuyBalance() == null) {
                sender.sendTextMes(bot, user, translations.get("buying-account-not-found-on-the-current"));
                return supportedState();
            }
            var data = registry.getOrCreate(user, PurchaseFreezeData::new);
            data.setTokenId(id);
            data.setAccount(account);
            return UserMenu.ACCOUNTS_PURCHASE_FREEZE_PERIOD;
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, translations.get("could-not-check-the-account-try-again"));
            return supportedState();
        }
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS_MORE;
    }
}
