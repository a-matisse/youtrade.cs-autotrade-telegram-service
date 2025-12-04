package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.ScoringRemoveData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.ScoringRemoveRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetProfitDto;
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
                        Пожалуйста, введите scoring-ID (целое число)...
                        
                        Список ваших scoring-ID:
                        %s
                        """,
                getProfitStr(user)
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
        long profitId;
        try {
            profitId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, ScoringRemoveData::new);
        data.setProfitId(profitId);
        return UserMenu.SCORING_REMOVE_STAGE_P;
    }

    private String getProfitStr(UserData user) {
        var restAns = endpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var profitData = fcd.getData().getProfitData();
        if (profitData.isEmpty())
            return "Список profit-ID пуст...";

        return profitData
                .stream()
                .map(FcdParamsGetProfitDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
