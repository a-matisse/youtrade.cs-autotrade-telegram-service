package cs.youtrade.autotrade.client.telegram.menu.start.ref.payout;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

@Service
public class RefPayoutState extends YTPTextMenuState<RefPayoutMenu> {
    private static final String SUPPORT_URL = "https://t.me/youtradecs_sup";
    private final RefEndpoint endpoint;

    public RefPayoutState(UserTextMessageSender sender, RefEndpoint endpoint) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_PAYOUT;
    }

    @Override
    public RefPayoutMenu getOption(String optionStr) {
        return RefPayoutMenu.valueOf(optionStr);
    }

    @Override
    public RefPayoutMenu[] getOptions(UserData userData) {
        return RefPayoutMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, RefPayoutMenu option) {
        return UserMenu.REF;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var answer = endpoint.getBalance(user.getChatId());
        if (answer.getStatus() >= 300 || answer.getResponse() == null)
            return null;

        var data = answer.getResponse();
        BigDecimal available = data.getReferralBalance() == null ? BigDecimal.ZERO : data.getReferralBalance();
        return String.format(Locale.US, """
                        %s <i>Выплата вознаграждения</i>

                        %s <b>Доступно к выплате</b>
                        <blockquote>• Реферальный баланс: <b>$%,.2f</b></blockquote>

                        %s <b>Как получить выплату</b>
                        <blockquote>Напишите в поддержку, что хотите вывести реферальное вознаграждение. Укажите <b>ID пользователя</b> и <b>сумму выплаты</b>.

                        Сотрудник уточнит реквизиты и комиссию перед выплатой.</blockquote>

                        %s <b>Ваш ID:</b> <code>%d</code>
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                DynamicEmoji.MONEY.getEmoji(), available,
                DynamicEmoji.NOTE.getEmoji(),
                DynamicEmoji.PROFILE.getEmoji(), data.getTdId()
        );
    }

    @Override
    public Map<RefPayoutMenu, String> getUrls(UserData userData) {
        return Map.of(RefPayoutMenu.SUPPORT, SUPPORT_URL);
    }
}
