package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.rename.stage1;

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
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender, new YTPPageProcessor(accountsV2Endpoint, paramsEndpoint));
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_RENAME_STAGE_1;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.ACCOUNTS;
        }

        String input = update.getMessage().getText();
        long tokenId;
        try {
            tokenId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.ACCOUNTS;
        }

        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setId(tokenId);
        return UserMenu.ACCOUNTS_RENAME_STAGE_2;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        long chatId = userData.getChatId();
        try {
            // Returning the message
            var dto = pageProcessor.getPage(chatId);
            String accountsStr = dto.getAccountsListStr();
            return String.format("""                        
                            %s <b>Теперь отправьте ID аккаунта для смены имени в Y.CS</b>
                            <blockquote>• Формат сообщения — <code>%s</code></blockquote>
                            
                            %s <i>Имя поменяется сразу у аккаунтов покупки, продажи и воркера</i>
                            <blockquote expandable>%s</blockquote>
                            """,
                    DynamicEmoji.WRITE.getEmoji(),
                    getRandomNumbersAsString(dto, 1),
                    DynamicEmoji.SUCCESS.getEmoji(),
                    accountsStr
            );
        } catch (RuntimeException e) {
            // Catching the error and sending the user
            log.error(e);
            return "Произошла непредвиденная ошибка, попробуйте снова (/accounts)...";
        }
    }
}
