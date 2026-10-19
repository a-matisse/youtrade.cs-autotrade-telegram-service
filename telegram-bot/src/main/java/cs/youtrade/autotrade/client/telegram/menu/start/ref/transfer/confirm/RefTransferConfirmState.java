package cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.confirm;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.RefTransferRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Locale;

@Service
public class RefTransferConfirmState extends YTPTextMenuState<RefTransferConfirmMenu> {
    private final RefTransferRegistry registry;

    public RefTransferConfirmState(UserTextMessageSender sender, RefTransferRegistry registry, BotTexts botTexts) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_TRANSFER_CONFIRM;
    }

    @Override
    public RefTransferConfirmMenu getOption(String optionStr) {
        return RefTransferConfirmMenu.valueOf(optionStr);
    }

    @Override
    public RefTransferConfirmMenu[] getOptions(UserData userData) {
        return RefTransferConfirmMenu.values();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "referral.transfer.RefTransferConfirm");
        return registry.get(user)
                .map(data -> String.format(Locale.US, """
                                %s <b>%s</b>

                                %s <b>%s</b>
                                <blockquote>• %s: <b>$%,.2f</b>
                                • %s: <b>$0.00</b>
                                • %s: <b>%s</b></blockquote>

                                <i>%s</i>
                                """,
                        DynamicEmoji.QUESTION.getEmoji(), translations.get("title"),
                        DynamicEmoji.MONEY.getEmoji(), translations.get("operation"),
                        translations.get("amount"), data.amount(), translations.get("fee"),
                        translations.get("destination"), translations.get("service-balance"), translations.get("transfer-terms")))
                .orElse(translations.get("transfer-not-found-return-to-the-referral"));
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, RefTransferConfirmMenu option) {
        return switch (option) {
            case CONFIRM -> registry.get(user).isPresent() ? UserMenu.REF_TRANSFER_PROCEED : UserMenu.REF;
            case CANCEL -> {
                registry.clear(user);
                yield UserMenu.REF;
            }
        };
    }
}
