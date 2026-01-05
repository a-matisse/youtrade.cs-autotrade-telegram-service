package cs.youtrade.autotrade.client.telegram.menu.start.user.params.portfolio.selling.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.portfolio.selling.TableSellingRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.DeleteAnsDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellListEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class TableSellingProceedState extends AbstractTerminalTextMenuState {
    private final TableSellingRegistry registry;
    private final SellListEndpoint endpoint;

    public TableSellingProceedState(
            UserTextMessageSender sender,
            TableSellingRegistry registry,
            SellListEndpoint endpoint) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_SELLING_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        var data = registry.remove(user);
        var restAns = endpoint.postList(chatId, data.getDtos());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        String ans = fcd
                .getData()
                .stream()
                .filter(dto -> dto.getCount() > 0)
                .map(this::createPostMessageAns)
                .collect(Collectors.joining("\n"));

        return ans.isEmpty()
                ? "#0: Не найдены предметы к удалению"
                : ans;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    private String createPostMessageAns(DeleteAnsDto info) {
        return String.format("Токен [%s]: %d предметов удалено", info.getGivenName(), info.getCount());
    }
}
