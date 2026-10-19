package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.choose;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
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
            UserTokenDeleteRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
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
        var translations = botTexts.forUser(userData, "accounts.TokenDelete");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote>%s
                        %s
                        %s</blockquote>
                        
                        <i>%s</i>
                        """,
                        DynamicEmoji.CHOOSE.getEmoji(),
                        translations.get("choose-what-to-delete"),
                        translations.format("buying-b-deletes-everything-buying-selling-and", Map.of("emphasis",
                                String.format("<b>%s %s</b>", DynamicEmoji.ITEM_RECEIVE.getEmoji(), translations.get("delete-buying")))),
                        translations.format("selling-b-deletes-selling-and-y-cs", Map.of("emphasis",
                                String.format("<b>%s %s</b>", DynamicEmoji.ITEM_SEND.getEmoji(), translations.get("delete-selling")))),
                        translations.format("y-cs-worker-b-deletes-only-the", Map.of("emphasis",
                                String.format("<b>%s %s</b>", DynamicEmoji.WORKER.getEmoji(), translations.get("delete-worker")))),
                        translations.format("cascading-deletion-b-a-higher-level-deletes", Map.of("emphasis",
                                String.format("<b>%s %s</b>", DynamicEmoji.WARNING.getEmoji(), translations.get("cascading-deletion")))));
    }
}
