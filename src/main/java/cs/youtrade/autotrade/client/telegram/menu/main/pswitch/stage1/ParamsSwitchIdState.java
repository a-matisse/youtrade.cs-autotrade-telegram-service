package cs.youtrade.autotrade.client.telegram.menu.main.pswitch.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.pswitch.ParamsSwitchData;
import cs.youtrade.autotrade.client.telegram.menu.main.pswitch.ParamsSwitchRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetProfitDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsListDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class ParamsSwitchIdState extends AbstractTextState {
    private final ParamsSwitchRegistry registry;
    private final ParamsEndpoint endpoint;

    public ParamsSwitchIdState(
            UserTextMessageSender sender,
            ParamsSwitchRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    protected String getMessage(UserData user) {
        return String.format("""
                        Пожалуйста, введите params-ID для переключения...
                        
                        Список ваших params-ID:
                        %s
                        """,
                getParamsStr(user)
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_SWITCH_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение в меню (/menu).");
            return UserMenu.MAIN;
        }

        String input = update.getMessage().getText();
        var data = registry.getOrCreate(user, ParamsSwitchData::new);
        data.setInput(input);
        return UserMenu.MAIN_PARAMETERS_SWITCH_STAGE_P;
    }

    private String getParamsStr(UserData user) {
        var restAns = endpoint.listParams(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = fcd.getData();
        if (data.isEmpty())
            return "Список profit-ID пуст...";

        return data
                .stream()
                .map(FcdParamsListDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
