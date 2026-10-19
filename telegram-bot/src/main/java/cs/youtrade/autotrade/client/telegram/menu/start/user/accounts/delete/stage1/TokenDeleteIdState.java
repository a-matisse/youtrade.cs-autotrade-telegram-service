package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPAccountPageTextMenuState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class TokenDeleteIdState extends YTPAccountPageTextMenuState {
    private final UserTokenDeleteRegistry registry;

    public TokenDeleteIdState(
            UserTextMessageSender sender,
            UserTokenDeleteRegistry registry,
            AccountsV2Endpoint endpoint,
            ParamsEndpoint paramsEndpoint,
            BotTexts botTexts
    ) {
        super(sender, new YTPPageProcessor(endpoint, paramsEndpoint), botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_REMOVE_STAGE_1;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.TokenDelete");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.ACCOUNTS;
        }
        // Preparing the input
        String input = update.getMessage().getText();
        List<Long> tokenIds = new ArrayList<>();
        // Getting ids
        String[] parts = input.split("\\s+");
        for (String part : parts) {
            try {
                tokenIds.add(Long.parseLong(part));
            } catch (NumberFormatException e) {
                // Мусор отбрасываем, просто игнорируем
                log.debug("Skipping invalid number: {}", part);
            }
        }
        // If no correct ids were found - skipping
        if (tokenIds.isEmpty()) {
            sender.sendTextMes(bot, userData, String.format("#1: %s",
                    translations.get("the-values-entered-are-not-numbers-going")));
            return UserMenu.ACCOUNTS;
        }
        // Adding ids to the registry to complete on the next stage
        var data = registry.getOrCreate(userData, UserTokenDeleteData::new);
        data.setTokenIds(tokenIds);
        return UserMenu.ACCOUNTS_REMOVE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.TokenDelete");
        long chatId = userData.getChatId();
        try {
            // Returning the message
            var dto = pageProcessor.getPage(chatId);
            String accountsStr = dto.getAccountsListStr(translations.inBundle("accounts.AccountOptions"));
            var opt = registry.getOrCreate(userData, UserTokenDeleteData::new).getOpt();
            return String.format("""
                            %s <b>%s</b>
                            <blockquote>• %s
                            • %s: <b>%s</b></blockquote>
                            
                            %s <i>%s</i>
                            <blockquote expandable>%s</blockquote>
                            """,
                            DynamicEmoji.WRITE.getEmoji(),
                            translations.get("now-send-account-ids-from-the-list"),
                            translations.format("message-format", Map.of("value1", String.format("<code>%s</code>", getRandomNumbersAsString(dto, 3, translations.inBundle("accounts.AccountOptions"))))),
                            translations.get("mode"),
                            opt.getDynamicEmoji() + " " + translations.inBundle("accounts.AccountOptions").get("button." + opt.name()),
                            DynamicEmoji.WARNING.getEmoji(),
                            translations.format("caution-b-accounts-are-deleted-along-with", Map.of("emphasis", String.format("<b>%s</b>", translations.get("caution-b-accounts-are-deleted-along-with.emphasis")))),
                            accountsStr);
        } catch (RuntimeException e) {
            // Catching the error and sending the user
            log.error(e);
            return translations.get("an-unexpected-error-occurred-try-again-accounts");
        }
    }
}
