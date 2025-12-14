package cs.youtrade.autotrade.client.telegram.menu.start.ref;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdRefDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserRefState extends AbstractTextMenuState<UserRefMenu> {
    private final RefEndpoint endpoint;

    public UserRefState(
            UserTextMessageSender sender,
            RefEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF;
    }

    @Override
    public UserRefMenu getOption(String optionStr) {
        return UserRefMenu.valueOf(optionStr);
    }

    @Override
    public UserRefMenu[] getOptions() {
        return UserRefMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserRefMenu t) {
        return switch (t) {
            case REF_CONNECT -> UserMenu.REF_CONNECT_STAGE_1;
            case REF_CREATE -> UserMenu.REF_CREATE;
            case RETURN -> UserMenu.START;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var ans = endpoint.refGet(user.getChatId());
        if (ans.getStatus() >= 300)
            return null;

        var fcd = ans.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = fcd.getData();
        return String.format("""
                        📊 <u><b>Реферальная система</b></u>
                        
                        %s
                        %s
                        """,
                getThisData(data),
                getUsedData(data)
        );
    }

    private String getThisData(FcdRefDto data) {
        if (data.getThisRef() == null || data.getThisRef().isEmpty())
            return "🔴 Реферальный код не создан";

        return String.format("""
                        📝 Ваша ссылка <code>%s</code>
                        💰 Ваш процент с рефералов: <b>%s%%</b>
                        📈 Бонус по коду: <b>$%s</b>
                        """,
                data.getThisRef(),
                data.getRefRate(),
                data.getRefReward()
        );
    }

    private String getUsedData(FcdRefDto data) {
        if (data.getUsedRef() == null || data.getUsedRef().isEmpty())
            return "🔴 Реферальный код не подключен";

        return String.format("""
                        🔗 Подключен <tg-spoiler>(%s)</tg-spoiler>
                        """,
                data.getUsedRef()
        );
    }
}
