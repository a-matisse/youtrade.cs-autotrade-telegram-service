package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.transfer.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.transfer.UserTokenTransferRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPAccountPageTextMenuState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdAccountsTransferInput;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class TokenTransferChooseState extends YTPAccountPageTextMenuState {
    private final UserTokenTransferRegistry registry;

    public TokenTransferChooseState(
            UserTextMessageSender sender,
            AccountsV2Endpoint accountsV2Endpoint,
            ParamsEndpoint paramsEndpoint,
            UserTokenTransferRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, new YTPPageProcessor(accountsV2Endpoint, paramsEndpoint), botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_TRANSFER_STAGE_1;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.TokenTransfer");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.ACCOUNTS;
        }
        // 1. Парсим ID токенов
        List<Long> tokenIds = Arrays.stream(update
                .getMessage()
                .getText()
                .split(" "))
                .map(str -> {
                    try {
                        return Long.parseLong(str);
                    } catch (NumberFormatException e) {
                        sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                                translations.get("the-value-entered-is-not-a-number"),
                                str));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        if (tokenIds.isEmpty())
            return UserMenu.ACCOUNTS;
        // 2. Присваиваем в
        var data = registry.getOrCreate(user, FcdAccountsTransferInput::new);
        data.setTokenIds(tokenIds);
        return UserMenu.ACCOUNTS_TRANSFER_STAGE_2;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.TokenTransfer");
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
                            translations.get("send-a-list-of-account-ids-to"),
                            translations.format("message-format", Map.of("value1", String.format("<code>%s</code>", getRandomNumbersAsString(dto, 3, translations.inBundle("accounts.AccountOptions"))))),
                            DynamicEmoji.SUCCESS.getEmoji(),
                            translations.get("accounts-will-be-moved-with-all-their"),
                            accountsStr);
        } catch (RuntimeException e) {
            // Catching the error and sending the user
            log.error(e);
            return translations.get("an-unexpected-error-occurred-try-again-accounts");
        }
    }
}
