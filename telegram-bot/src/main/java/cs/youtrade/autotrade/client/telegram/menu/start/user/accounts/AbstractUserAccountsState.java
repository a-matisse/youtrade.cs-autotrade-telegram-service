package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import java.util.Map;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Log4j2
public abstract class AbstractUserAccountsState<MENU extends IMenuEnum> extends YTPTextMenuState<MENU> {
    protected final YTPPageProcessor pageProcessor;

    protected AbstractUserAccountsState(UserTextMessageSender sender, YTPPageProcessor pageProcessor, BotTexts botTexts) {
        super(sender, botTexts);
        this.pageProcessor = pageProcessor;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.UserAccounts");
        try {
            var dto = pageProcessor.getPage(user.getChatId());
            return String.format("""
                            %s <i>%s</i>

                            %s <b>%s</b>
                            <blockquote expandable>%s</blockquote>

                            %s
                            """,
                            DynamicEmoji.YOUTRADE.getEmoji(),
                            translations.format("account-management", Map.of("argument2", String.valueOf(dto.pageData().getPage() + 1), "argument3", String.valueOf(dto.fcd().getAccounts().getTotalPages()))),
                            DynamicEmoji.STEAM.getEmoji(),
                            translations.get("accounts"),
                            dto.getAccountsListStr(translations.inBundle("accounts.AccountOptions")),
                            dto.params().getDirection());
        } catch (RuntimeException e) {
            log.error(e);
            return translations.get("an-unexpected-error-occurred-try-again-accounts");
        }
    }
}
