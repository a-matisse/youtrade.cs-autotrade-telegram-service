package cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.confirm;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.RefTransferRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Locale;

@Service
public class RefTransferConfirmState extends YTPTextMenuState<RefTransferConfirmMenu> {
    private final RefTransferRegistry registry;

    public RefTransferConfirmState(UserTextMessageSender sender, RefTransferRegistry registry) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_TRANSFER_CONFIRM;
    }

    @Override
    public RefTransferConfirmMenu getOption(String optionStr) {
        return RefTransferConfirmMenu.valueOf(optionStr);
    }

    @Override
    public RefTransferConfirmMenu[] getOptions(UserData userData) {
        return RefTransferConfirmMenu.values();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return registry.get(user)
                .map(data -> String.format(Locale.US, """
                                %s <b>Подтверждение перевода</b>

                                %s <b>Операция</b>
                                <blockquote>• Сумма: <b>$%,.2f</b>
                                • Комиссия: <b>$0.00</b>
                                • Зачисление: <b>на баланс сервиса</b></blockquote>

                                <i>Реферальное вознаграждение переводится один к одному и не получает бонус к пополнению.</i>
                                """,
                        DynamicEmoji.QUESTION.getEmoji(),
                        DynamicEmoji.MONEY.getEmoji(),
                        data.amount()))
                .orElse("Операция перевода не найдена. Вернитесь в реферальный кабинет.");
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, RefTransferConfirmMenu option) {
        return switch (option) {
            case CONFIRM -> registry.get(user).isPresent() ? UserMenu.REF_TRANSFER_PROCEED : UserMenu.REF;
            case CANCEL -> {
                registry.clear(user);
                yield UserMenu.REF;
            }
        };
    }
}
