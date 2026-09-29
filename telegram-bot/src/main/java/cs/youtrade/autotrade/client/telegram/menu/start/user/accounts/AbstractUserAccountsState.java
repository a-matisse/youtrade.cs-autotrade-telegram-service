package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

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

    protected AbstractUserAccountsState(UserTextMessageSender sender, YTPPageProcessor pageProcessor) {
        super(sender);
        this.pageProcessor = pageProcessor;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        try {
            var dto = pageProcessor.getPage(user.getChatId());
            return String.format("""
                            %s <i>Управление аккаунтами (%s/%s)</i>

                            %s <b>Аккаунты</b>
                            <blockquote expandable>%s</blockquote>

                            %s
                            """,
                    DynamicEmoji.YOUTRADE.getEmoji(), dto.pageData().getPage() + 1,
                    dto.fcd().getAccounts().getTotalPages(),
                    DynamicEmoji.STEAM.getEmoji(), dto.getAccountsListStr(),
                    dto.params().getDirection());
        } catch (RuntimeException e) {
            log.error(e);
            return "Произошла непредвиденная ошибка, попробуйте снова (/accounts)...";
        }
    }
}
