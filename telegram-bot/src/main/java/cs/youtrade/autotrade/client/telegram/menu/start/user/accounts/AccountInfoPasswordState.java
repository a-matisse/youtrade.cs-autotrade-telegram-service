package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AccountInfoPasswordState extends AbstractAccountInfoState {
    private final AccountsV2Endpoint endpoint;
    private final AccountInfoResultState resultState;

    public AccountInfoPasswordState(UserTextMessageSender sender, AccountInfoRegistry registry,
                                    AccountsV2Endpoint endpoint, AccountInfoResultState resultState, BotTexts botTexts) {
        super(sender, registry, botTexts);
        this.endpoint = endpoint;
        this.resultState = resultState;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_INFO_PASSWORD; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.AccountInfo");
        return String.format("""
                %s%s <b>%s</b>
                """,
                summary(user),
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("send-your-security-password"));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.AccountInfo");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        var data = registry.getOrCreate(user, AccountInfoData::new);
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        String password = update.getMessage().getText();
        if (password.isEmpty()) {
            sender.sendTextMes(bot, user, translations.get("the-security-password-cannot-be-empty"));
            return supportedState();
        }
        synchronized (data) {
            if (data.getResult() != null) return UserMenu.ACCOUNTS_INFO_RESULT;
            try {
                var answer = endpoint.exportCredentials(user.getChatId(), data.getTokenId(), password);
                if (answer.getStatus() == 200 && answer.getResponse() != null
                        && answer.getResponse().getAccount() != null) {
                    resultState.sendDetails(bot, user, answer.getResponse());
                    data.setPasswordVerified(true);
                    data.setResult(AccountInfoData.Result.SENT);
                    return UserMenu.ACCOUNTS_INFO_RESULT;
                }
                if (answer.getStatus() == 429) {
                    data.setResult(AccountInfoData.Result.RATE_LIMITED);
                    return UserMenu.ACCOUNTS_INFO_RESULT;
                }
                sender.sendTextMes(bot, user, switch (answer.getStatus()) {
                    case 403 -> translations.get("incorrect-security-password");
                    case 404 -> translations.get("the-account-was-not-found-or-no");
                    case 502 -> translations.get("could-not-retrieve-steam-details-try-again");
                    default -> translations.get("could-not-retrieve-account-details-try-again");
                });
            } catch (RuntimeException error) {
                sender.sendTextMes(bot, user, translations.get("could-not-retrieve-account-details-try-again"));
            }
            return supportedState();
        }
    }
}
