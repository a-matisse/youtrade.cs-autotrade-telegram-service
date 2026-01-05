package cs.youtrade.autotrade.client.telegram.menu.start.user.params.portfolio.restrict.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.portfolio.restrict.TableRestrictRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.DeleteAnsDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellRestrictEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TableRestrictProceedState extends AbstractTerminalTextMenuState {
    private final TableRestrictRegistry registry;
    private final SellRestrictEndpoint endpoint;

    public TableRestrictProceedState(
            UserTextMessageSender sender,
            TableRestrictRegistry registry,
            SellRestrictEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_RESTRICT_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        var data = registry.remove(user);
        var restAns = endpoint.postRestrictions(chatId, data.getDtos());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return fcd
                .getData()
                .stream()
                .filter(Objects::nonNull)
                .filter(info -> info.getCount() > 0)
                .map(this::createPostMessageAns)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    private String createPostMessageAns(DeleteAnsDto info) {
        return String.format("Токен [%s]: %d предметов запрещено", info.getGivenName(), info.getCount());
    }
}