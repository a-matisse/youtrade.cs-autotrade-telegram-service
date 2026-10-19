package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AccountInfoState extends AbstractAccountInfoState {
    private final YTPPageProcessor pageProcessor;

    public AccountInfoState(UserTextMessageSender sender, AccountInfoRegistry registry,
                            YTPPageProcessor pageProcessor, BotTexts botTexts) {
        super(sender, registry, botTexts);
        this.pageProcessor = pageProcessor;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_INFO;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.AccountInfo");
        if (!user.isAccountExportPasswordSet())
            return String.format("""
                            %s <i>%s</i>

                            %s <b>%s</b>
                            <blockquote>• %s</blockquote>

                            %s <b>%s</b>
                            """,
                            DynamicEmoji.YOUTRADE.getEmoji(),
                            translations.get("account-details"),
                            DynamicEmoji.SECURE.getEmoji(),
                            translations.get("access"),
                            translations.format("security-password", Map.of("value1", String.format("<b>%s</b>", translations.get("not-set")))),
                            DynamicEmoji.WRITE.getEmoji(),
                            translations.get("set-a-password-in-prefs"));
        try {
            return String.format("""
                            %s%s <b>%s</b>
                            <blockquote expandable>%s</blockquote>
                            """,
                            summary(user),
                            DynamicEmoji.WRITE.getEmoji(),
                            translations.get("send-the-account-id"),
                            pageProcessor.getPage(user.getChatId()).getAccountsListStr(translations.inBundle("accounts.AccountOptions")));
        } catch (RuntimeException error) {
            return String.format("""
                            %s%s
                            """,
                            summary(user),
                            translations.get("could-not-load-accounts-try-again"));
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.AccountInfo");
        if (!user.isAccountExportPasswordSet()) return supportedState();
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        try {
            long id = Long.parseLong(update.getMessage().getText().trim());
            boolean accountsExist = pageProcessor
                    .getPage(user.getChatId())
                    .fcd()
                    .getAccounts()
                    .stream()
                    .noneMatch(account -> account.getId().equals(id));
            if (accountsExist) {
                sender.sendTextMes(bot, user, translations.get("account-not-found-on-the-current-page"));
                return supportedState();
            }
            registry.getOrCreate(user, AccountInfoData::new).setTokenId(id);
            return UserMenu.ACCOUNTS_INFO_PASSWORD;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, translations.get("enter-one-account-id-as-a-number"));
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, translations.get("could-not-check-the-account-try-again"));
        }
        return supportedState();
    }
}
