package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.ScoringEditData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.edit.ScoringEditRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetScoringDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class ScoringEditIdState extends YTPTextState {
    private final ScoringEditRegistry registry;
    private final ParamsEndpoint endpoint;

    public ScoringEditIdState(
            UserTextMessageSender sender,
            ScoringEditRegistry registry,
            ParamsEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.scoring.UserScoring");
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote expandable>%s</blockquote>
                        <b>%s</b>
                        """,
                        DynamicEmoji.CHOOSE.getEmoji(),
                        translations.get("choose-a-scoring-configuration-to-edit"),
                        getScoringStr(userData),
                        translations.get("enter-a-scoring-id-to-edit"));
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_EDIT_STAGE_1;
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
        long scoringId;
        try {
            scoringId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: %s: %s",
                    translations.get("the-value-entered-is-not-a-number"),
                    input));
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, ScoringEditData::new);
        data.setScoringId(scoringId);
        return UserMenu.SCORING_EDIT_STAGE_2;
    }

    private String getScoringStr(UserData user) {
        var translations = botTexts.forUser(user, "parameters.scoring.UserScoring");
        var restAns = endpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = fcd.getData().getScoringData();
        if (data.isEmpty())
            return translations.get("the-scoring-id-list-is-empty");

        return data
                .stream()
                .map(value -> value.asMessage(translations.inBundle("summary.ParamsSummary")))
                .collect(Collectors.joining("\n"));
    }
}
