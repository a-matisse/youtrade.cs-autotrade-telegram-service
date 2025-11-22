package cs.youtrade.autotrade.client.telegram.prototype.def;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public abstract class AbstractDefState<MESSAGE> implements DefStateInt<UserData, UserMenu, MESSAGE> {
    protected static final String SERVER_ERROR_MES = "🚫 Сервер временно недоступен. Попробуйте через несколько минут.";
    protected final TelegramSendMessageService sender;
}
