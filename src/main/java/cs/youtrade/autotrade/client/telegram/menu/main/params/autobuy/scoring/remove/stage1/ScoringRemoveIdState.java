package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.ScoringRemoveData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.ScoringRemoveRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetScoringDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class ScoringRemoveIdState extends AbstractTextState {
    private final ScoringRemoveRegistry registry;
    private final ParamsEndpoint endpoint;

    public ScoringRemoveIdState(
            UserTextMessageSender sender,
            ScoringRemoveRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    protected String getMessage(UserData user) {
        return String.format("""
                        Список ваших scoring-ID:
                        %s
                        
                        Пожалуйста, введите scoring-ID (целое число)...
                        """,
                getScoringStr(user)
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_REMOVE_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.SCORING;
        }

        String input = update.getMessage().getText();
        long scoringId;
        try {
            scoringId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, ScoringRemoveData::new);
        data.setScoringId(scoringId);
        return UserMenu.SCORING_REMOVE_STAGE_P;
    }

    private String getScoringStr(UserData user) {
        var restAns = endpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = fcd.getData().getScoringData();
        if (data.isEmpty())
            return "Список scoring-ID пуст...";

        return data
                .stream()
                .map(FcdParamsGetScoringDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
