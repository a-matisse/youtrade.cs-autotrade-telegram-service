package cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.amount;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.RefTransferRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdReferralBalanceDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefTransferAmountState extends YTPTextMenuState<RefTransferAmountMenu> {
    private final RefEndpoint endpoint;
    private final RefTransferRegistry registry;
    private final Map<Long, BigDecimal> balances = new ConcurrentHashMap<>();

    public RefTransferAmountState(UserTextMessageSender sender, RefEndpoint endpoint, RefTransferRegistry registry) {
        super(sender);
        this.endpoint = endpoint;
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_TRANSFER_AMOUNT;
    }

    @Override
    public RefTransferAmountMenu getOption(String optionStr) {
        return RefTransferAmountMenu.valueOf(optionStr);
    }

    @Override
    public RefTransferAmountMenu[] getOptions(UserData userData) {
        return RefTransferAmountMenu.values();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var answer = endpoint.getBalance(user.getChatId());
        if (answer.getStatus() >= 300 || answer.getResponse() == null)
            return null;
        FcdReferralBalanceDto data = answer.getResponse();
        BigDecimal available = valueOrZero(data.getReferralBalance());
        balances.put(user.getChatId(), available);
        return String.format(Locale.US, """
                        %s <i>Перевод вознаграждения</i>

                        %s <b>Доступно для перевода</b>
                        <blockquote>• Реферальный баланс: <b>$%,.2f</b></blockquote>

                        %s <b>Введите сумму в $ (USD)</b>
                        └ <i>Перевод выполняется без комиссии и бонуса к пополнению</i>
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                DynamicEmoji.MONEY.getEmoji(), available,
                DynamicEmoji.WRITE.getEmoji()
        );
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, RefTransferAmountMenu option) {
        return switch (option) {
            case ALL -> prepare(user, balances.getOrDefault(user.getChatId(), BigDecimal.ZERO));
            case RETURN -> UserMenu.REF;
        };
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Введите сумму числом или нажмите кнопку ниже.");
            return UserMenu.REF_TRANSFER_AMOUNT;
        }
        try {
            BigDecimal amount = new BigDecimal(update.getMessage().getText().trim().replace(',', '.'));
            BigDecimal available = balances.getOrDefault(user.getChatId(), BigDecimal.ZERO);
            if (amount.signum() <= 0 || amount.scale() > 2 || amount.compareTo(available) > 0) {
                sender.sendTextMes(bot, user, "Сумма должна быть больше $0.00, не превышать доступный баланс и содержать не более двух знаков после запятой.");
                return UserMenu.REF_TRANSFER_AMOUNT;
            }
            return prepare(user, amount);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, "Не удалось распознать сумму. Пример: <code>10.00</code>");
            return UserMenu.REF_TRANSFER_AMOUNT;
        }
    }

    private UserMenu prepare(UserData user, BigDecimal amount) {
        if (amount.signum() <= 0)
            return UserMenu.REF;
        registry.create(user, amount);
        return UserMenu.REF_TRANSFER_CONFIRM;
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
