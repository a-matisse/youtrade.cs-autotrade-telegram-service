package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.restore.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.telegram.buttons.sender.text.BaseTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableV2RestoreAgreementState extends YTPTextMenuState<TableV2RestoreAgreementMenu> {
    public TableV2RestoreAgreementState(BaseTextMessageSender<UserData> sender, BotTexts botTexts) {
        super(sender, botTexts);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_V2_RESTORE_STAGE_1;
    }

    @Override
    public TableV2RestoreAgreementMenu getOption(String optionStr) {
        return TableV2RestoreAgreementMenu.valueOf(optionStr);
    }

    @Override
    public TableV2RestoreAgreementMenu[] getOptions(UserData userData) {
        return TableV2RestoreAgreementMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, TableV2RestoreAgreementMenu t) {
        return switch (t) {
            case AGREE -> UserMenu.PORTFOLIO_V2_RESTORE_STAGE_P;
            case RETURN -> UserMenu.PORTFOLIO;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.TableV2Restore");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s
                        • %s
                        • %s
                        • %s</blockquote>
                        """,
                        DynamicEmoji.QUESTION.getEmoji(),
                        translations.get("are-you-sure-you-want-to-start"),
                        translations.get("all-items-not-on-the-exclusion-list"),
                        translations.get("the-bot-will-estimate-purchase-prices-using"),
                        translations.get("if-no-match-is-found-the-bot"),
                        translations.get("by-tapping-i-agree-you-accept-the"));
    }
}
