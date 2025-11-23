package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit.ScoringEditRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.profit.ProfitEndpoint;
import org.springframework.stereotype.Service;

@Service
public class ScoringEditProceedState extends AbstractTerminalTextMenuState {
    private final ScoringEditRegistry registry;
    private final ProfitEndpoint endpoint;

    public ScoringEditProceedState(
            UserTextMessageSender sender,
            ScoringEditRegistry registry,
            ProfitEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_EDIT_STAGE_P;
    }

    @Override
    public String getHeaderText(UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.editProfit(user.getChatId(), data.getProfitId(), data.getField(), data.getValue());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();


        return String.format("Обновлено profit-ID=%s", fcd.getData());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.SCORING;
    }
}
