package cs.youtrade.autotrade.client.telegram.menu.main.list;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsListDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParamsListState extends AbstractTerminalTextMenuState {
    private final ParamsEndpoint endpoint;

    public ParamsListState(
            UserTextMessageSender sender,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_LIST;
    }

    @Override
    public String getHeaderText(UserData userData) {
        var restAns = endpoint.listParams(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return getStringFromFcd(fcd);
    }

    @Override
    public UserMenu retState() {
        return UserMenu.MAIN;
    }

    private String getStringFromFcd(FcdDefaultDto<List<FcdParamsListDto>> fcd) {
        return fcd
                .getData()
                .stream()
                .map(p -> String.format("""
                                ────────────────────────
                                🏷 Параметры ID %d
                                🏷 Имя: %s
                                🔸 Источник: %s
                                🔹 Назначение: %s
                                💰 Баланс аккаунта: $%,.2f
                                """,
                        p.getTdpId(),
                        p.getGivenName(),
                        p.getSource(),
                        p.getDestination(),
                        p.getBalance()
                ))
                .collect(Collectors.joining("\n"));
    }
}
