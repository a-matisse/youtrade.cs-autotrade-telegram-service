package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.rename.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPAccountPageTextMenuState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserRenameData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.rename.UserTokenRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Log4j2
public class TokenRenameIdState extends YTPAccountPageTextMenuState {
    private final UserTokenRenameRegistry registry;

    public TokenRenameIdState(
            UserTextMessageSender sender,
            UserTokenRenameRegistry registry,
            AccountsV2Endpoint accountsV2Endpoint,
            ParamsEndpoint paramsEndpoint,
            BotTexts botTexts
    ) {
        super(sender, new YTPPageProcessor(accountsV2Endpoint, paramsEndpoint), botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_RENAME_STAGE_1;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.TokenRename");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.ACCOUNTS;
        }

        String input = update.getMessage().getText();
        long tokenId;
        try {
            tokenId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                    translations.get("the-value-entered-is-not-a-number"),
                    input));
            return UserMenu.ACCOUNTS;
        }

        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setId(tokenId);
        return UserMenu.ACCOUNTS_RENAME_STAGE_2;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.TokenRename");
        long chatId = userData.getChatId();
        try {
            // Returning the message
            var dto = pageProcessor.getPage(chatId);
            String accountsStr = dto.getAccountsListStr(translations.inBundle("accounts.AccountOptions"));
            return String.format("""
                            %s <b>%s</b>
                            <blockquote>• %s</blockquote>
                            
                            %s <i>%s</i>
                            <blockquote expandable>%s</blockquote>
                            """,
                            DynamicEmoji.WRITE.getEmoji(),
                            translations.get("now-send-the-account-id-to-rename"),
                            translations.format("message-format", Map.of("value1", String.format("<code>%s</code>", getRandomNumbersAsString(dto, 1, translations.inBundle("accounts.AccountOptions"))))),
                            DynamicEmoji.SUCCESS.getEmoji(),
                            translations.get("the-name-will-change-for-buying-selling"),
                            accountsStr);
        } catch (RuntimeException e) {
            // Catching the error and sending the user
            log.error(e);
            return translations.get("an-unexpected-error-occurred-try-again-accounts");
        }
    }
}
