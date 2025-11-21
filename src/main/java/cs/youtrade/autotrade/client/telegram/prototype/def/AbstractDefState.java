package cs.youtrade.autotrade.client.telegram.prototype.def;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public abstract class AbstractDefState implements DefStateInt<Long, UserMenu> {
    protected final TelegramSendMessageService sender;
}
