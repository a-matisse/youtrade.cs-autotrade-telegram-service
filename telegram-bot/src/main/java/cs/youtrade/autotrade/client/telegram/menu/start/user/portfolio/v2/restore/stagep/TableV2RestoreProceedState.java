package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.restore.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.v2.SellV2RestoreEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableV2RestoreProceedState extends YTPTerminalTextMenuState {
    private final SellV2RestoreEndpoint endpoint;

    public TableV2RestoreProceedState(
            UserTextMessageSender sender,
            SellV2RestoreEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_V2_RESTORE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = endpoint.restore(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("""
                        %s <b>Восстановление предметов на площадке продажи запущено</b>
                        └ <i>Результаты поступят отдельными сообщениями</i>
                        """,
                DynamicEmoji.SUCCESS.getEmoji());
    }
}
