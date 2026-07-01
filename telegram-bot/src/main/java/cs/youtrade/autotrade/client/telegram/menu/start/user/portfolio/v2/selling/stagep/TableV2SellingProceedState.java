package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.TableV2SellingRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.v2.SellV2SellingEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableV2SellingProceedState extends YTPTerminalTextMenuState {
    private final SellV2SellingEndpoint endpoint;
    private final TableV2SellingRegistry registry;

    public TableV2SellingProceedState(
            UserTextMessageSender sender,
            SellV2SellingEndpoint endpoint,
            TableV2SellingRegistry registry
    ) {
        super(sender);
        this.endpoint = endpoint;
        this.registry = registry;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_V2_SELLING_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        long chatId = userData.getChatId();
        var data = registry.remove(userData);
        var restAns = endpoint.postSelling(chatId, data.getDtos());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("""
                        %s <b>Изменения предметов приняты в обработку</b>
                        └ <i>Результаты поступят отдельными сообщениями</i>
                        """,
                DynamicEmoji.SUCCESS.getEmoji());
    }
}
