package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

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
                            YTPPageProcessor pageProcessor) {
        super(sender, registry);
        this.pageProcessor = pageProcessor;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_INFO;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        if (!user.isAccountExportPasswordSet())
            return String.format("""
                            %s <i>Данные аккаунта</i>

                            %s <b>Доступ</b>
                            <blockquote>• Защитный пароль: <b>не установлен</b></blockquote>

                            %s <b>Установите пароль в /prefs</b>
                            """,
                    DynamicEmoji.YOUTRADE.getEmoji(),
                    DynamicEmoji.SECURE.getEmoji(),
                    DynamicEmoji.WRITE.getEmoji()
            );
        try {
            return String.format("""
                            %s%s <b>Отправьте ID аккаунта</b>
                            <blockquote expandable>%s</blockquote>
                            """,
                    summary(user),
                    DynamicEmoji.WRITE.getEmoji(),
                    pageProcessor.getPage(user.getChatId()).getAccountsListStr()
            );
        } catch (RuntimeException error) {
            return String.format("""
                            %sНе удалось загрузить аккаунты. Попробуйте снова.
                            """,
                    summary(user)
            );
        }
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!user.isAccountExportPasswordSet()) return supportedState();
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
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
                sender.sendTextMes(bot, user, "Аккаунт не найден на текущей странице.");
                return supportedState();
            }
            registry.getOrCreate(user, AccountInfoData::new).setTokenId(id);
            return UserMenu.ACCOUNTS_INFO_PASSWORD;
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Введите один ID аккаунта числом.");
        } catch (RuntimeException error) {
            sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
        }
        return supportedState();
    }
}
