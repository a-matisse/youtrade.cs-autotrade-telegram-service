package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.unfollow.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.unfollow.UserUnfollowData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.unfollow.UserUnfollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsFollowDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class UnfollowIdState extends AbstractTextState {
    private final UserUnfollowRegistry registry;
    private final ParamsEndpoint endpoint;

    public UnfollowIdState(
            UserTextMessageSender sender,
            UserUnfollowRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    protected String getMessage(UserData user) {
        return String.format("""
                        Пожалуйста, введите follow-ID, направления, от которого хотите отписаться...
                        
                        Список доступны follow-ID:
                        %s
                        """,
                getProfitStr(user)
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_UNFOLLOW_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.WORDS;
        }

        String input = update.getMessage().getText();
        long followId;
        try {
            followId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, UserUnfollowData::new);
        data.setFollowId(followId);
        return UserMenu.FOLLOW_UNFOLLOW_STAGE_P;
    }

    private String getProfitStr(UserData user) {
        var restAns = endpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = fcd.getData().getFollows();
        if (data.isEmpty())
            return "Список follow-ID пуст...";

        return data
                .stream()
                .map(FcdParamsFollowDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
