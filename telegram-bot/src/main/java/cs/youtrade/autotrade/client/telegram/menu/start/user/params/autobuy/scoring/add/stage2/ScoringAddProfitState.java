package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.ScoringAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add.ScoringAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ScoringAddProfitState extends YTPTextState {
    private final ScoringAddRegistry registry;

    public ScoringAddProfitState(
            UserTextMessageSender sender,
            ScoringAddRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.scoring.UserScoring");
        return String.format("%s <b>%s</b>",
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("now-enter-the-minimum-profitability-score-greater"));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_ADD_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "parameters.scoring.UserScoring");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.SCORING;
        }

        String input = update.getMessage().getText();
        double minProfit;
        try {
            minProfit = Double.parseDouble(input);
            if (minProfit < 90d) {
                sender.sendTextMes(bot, user, String.format("#2: %s",
                        translations.get("empty-message-received-going-back")));
                return UserMenu.SCORING;
            }
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                    translations.get("the-value-entered-is-not-a-number"),
                    input));
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, ScoringAddData::new);
        data.setMinProfit(minProfit / 100d);
        return UserMenu.SCORING_ADD_STAGE_P;
    }
}
