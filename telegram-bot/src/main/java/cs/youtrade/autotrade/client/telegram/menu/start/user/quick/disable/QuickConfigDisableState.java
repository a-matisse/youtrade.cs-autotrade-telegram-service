package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.disable;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsQuickConfigEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class QuickConfigDisableState extends YTPTerminalTextMenuState {
    private final ParamsQuickConfigEndpoint endpoint;

    public QuickConfigDisableState(
            UserTextMessageSender sender,
            ParamsQuickConfigEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.USER_QUICK_CONFIG_DISABLE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "parameters.QuickConfig");
        RestAnswer<FcdDefaultDto<Long>> restAns = endpoint.disable(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();
        return String.format("%s <b>%s</b>",
                DynamicEmoji.OFF.getEmoji(),
                translations.get("quickconfig-disabled"));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.USER;
    }
}
