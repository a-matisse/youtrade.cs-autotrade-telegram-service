package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.ScoringAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.scoring.ScoringEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class StageAddProceedState extends YTPTerminalTextMenuState {
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

        return String.format("%s <b>Создано scoring-оценивание с ID=<code>%s</code></b>",
                DynamicEmoji.SUCCESS.getEmoji(), fcd.getData());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.SCORING;
    }
}
