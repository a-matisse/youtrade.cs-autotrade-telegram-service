package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.remove.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.remove.ScoringRemoveRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.scoring.ScoringEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ScoringRemoveProceedState extends YTPTerminalTextMenuState {
    private final ScoringRemoveRegistry registry;
    private final ScoringEndpoint endpoint;

    public ScoringRemoveProceedState(
            UserTextMessageSender sender,
            ScoringRemoveRegistry registry,
            ScoringEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_REMOVE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.get(user);
        var restAns = endpoint.deleteScoring(user.getChatId(), data.getScoringId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("%s <b>Удалено scoring-оценивание ID=%d</b>",
                DynamicEmoji.SUCCESS.getEmoji(), fcd.getData());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.SCORING;
    }
}
