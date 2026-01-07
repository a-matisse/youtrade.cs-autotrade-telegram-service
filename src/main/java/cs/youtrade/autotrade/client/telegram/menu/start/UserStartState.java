package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserStartState extends AbstractTextMenuState<UserTextMenu> {
    private final GeneralEndpoint endpoint;

    public UserStartState(
            UserTextMessageSender sender,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserTextMenu getOption(String optionStr) {
        return UserTextMenu.valueOf(optionStr);
    }

    @Override
    public UserTextMenu[] getOptions() {
        return UserTextMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserTextMenu t) {
        return switch (t) {
            case USER -> UserMenu.USER;
            case REF -> UserMenu.REF;
            case TOP_UP -> UserMenu.TOP_UP_STAGE_1;
            case GET_PRICE -> UserMenu.GET_PRICE;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        // 1) Инициализация пользователя (если он не инициализирован)
        endpoint.initUser(user.getChatId());

        // 2) Приветствие
        var restAns = endpoint.viewAccInfo(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return String.format("""
                        👋 <b>YouTrade.CS — ваш ассистент по автоматизации торговли CS2</b>
                        🤖 Находит лучшие позиции, управляет сделками
                        ━━━━━━━━━━━━━━━━━━━━━
                        
                        👤 Ваш ID: <b>%s</b>
                        💰 Остаток баланса: <tg-spoiler>$%.2f</tg-spoiler>
                        """,
                fcd.getTdId(),
                fcd.getBalance()
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.START;
    }
}
