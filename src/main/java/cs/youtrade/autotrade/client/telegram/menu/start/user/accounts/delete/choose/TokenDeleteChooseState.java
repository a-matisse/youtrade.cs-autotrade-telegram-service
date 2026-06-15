package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.AccountsChooseOption;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TokenDeleteChooseState extends YTPTextMenuState<AccountsChooseOption> {
    private final UserTokenDeleteRegistry registry;

    public TokenDeleteChooseState(
            UserTextMessageSender sender,
            UserTokenDeleteRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_REMOVE_STAGE_CHOOSE;
    }

    @Override
    public AccountsChooseOption getOption(String optionStr) {
        return AccountsChooseOption.valueOf(optionStr);
    }

    @Override
    public AccountsChooseOption[] getOptions(UserData userData) {
        return AccountsChooseOption.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, AccountsChooseOption t) {
        var data = registry.getOrCreate(user, UserTokenDeleteData::new);
        data.setOpt(t);
        return switch (t) {
            case BUYER_ACCOUNT, SELLER_ACCOUNT, WORKER_ACCOUNT -> UserMenu.ACCOUNTS_REMOVE_STAGE_1;
            case RETURN -> UserMenu.ACCOUNTS;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Выберите режим удаления</b>
                        
                        <blockquote><b>%s Покупка</b> — удалит ВСЁ: покупателя, продавца и воркера
                        <b>%s Продажа</b> — удалит продавца и воркера (покупатель останется)
                        <b>%s Воркер</b> — удалит только воркера</blockquote>
                        
                        <i><b>%s Каскадное удаление</b>: более высокий уровень удаляет все зависимые уровни</i>
                        """,
                DynamicEmoji.CHOOSE.getEmoji(),
                DynamicEmoji.ITEM_RECEIVE.getEmoji(),
                DynamicEmoji.ITEM_SEND.getEmoji(),
                DynamicEmoji.WORKER.getEmoji(),
                DynamicEmoji.WARNING.getEmoji()
        );
    }
}
