package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.add.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.add.ScoringAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.scoring.ScoringEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class StageAddProceedState extends AbstractTerminalTextMenuState {
    private final ScoringAddRegistry registry;
    private final ScoringEndpoint scoringEndpoint;

    public StageAddProceedState(
            UserTextMessageSender sender,
            ScoringAddRegistry registry,
            ScoringEndpoint scoringEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.scoringEndpoint = scoringEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_ADD_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.remove(userData);
        var restAns = scoringEndpoint.addScoring(userData.getChatId(), data.getMinProfit(), data.getType());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return "Создано scoring-оценивание с ID=" + fcd.getData();
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }
}
