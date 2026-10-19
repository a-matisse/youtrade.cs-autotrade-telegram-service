package cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.proceed;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.RefTransferRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdReferralBalanceOperationDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Service
public class RefTransferProceedState extends YTPTextMenuState<RefTransferProceedMenu> {
    private final RefEndpoint endpoint;
    private final RefTransferRegistry registry;
    private final Map<Long, Boolean> completed = new ConcurrentHashMap<>();

    public RefTransferProceedState(UserTextMessageSender sender, RefEndpoint endpoint, RefTransferRegistry registry, BotTexts botTexts) {
        super(sender, botTexts);
        this.endpoint = endpoint;
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF_TRANSFER_PROCEED;
    }

    @Override
    public RefTransferProceedMenu getOption(String optionStr) {
        return RefTransferProceedMenu.valueOf(optionStr);
    }

    @Override
    public RefTransferProceedMenu[] getOptions(UserData userData) {
        return RefTransferProceedMenu.values();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "referral.transfer.RefTransferProceed");
        var pending = registry.get(user);
        if (pending.isEmpty()) {
            completed.put(user.getChatId(), true);
            return translations.get("the-transfer-has-already-been-completed-or");
        }

        var data = pending.get();
        var answer = endpoint.transferBalance(user.getChatId(), data.amount(), data.requestId());
        if (answer.getStatus() >= 300 || answer.getResponse() == null) {
            completed.put(user.getChatId(), false);
            return String.format("""
                            %s <b>%s</b>

                            <blockquote>%s</blockquote>
                            """,
                            DynamicEmoji.WARNING.getEmoji(),
                            translations.get("could-not-retrieve-the-transfer-result"),
                            translations.get("retry-using-the-button-below-the-same"));
        }

        FcdReferralBalanceOperationDto operation = answer.getResponse();
        if (!"COMPLETED".equals(operation.getStatus())) {
            completed.put(user.getChatId(), false);
            return String.format("""
                            %s <b>%s</b>

                            <blockquote>%s</blockquote>
                            """,
                            DynamicEmoji.WAIT.getEmoji(),
                            translations.get("transfer-still-processing"),
                            translations.format("operation-status-check-the-result-again-using", Map.of("value1", String.format("<b>%s</b>", operation.getStatus() == null ? "—" : operation.getStatus()))));
        }

        completed.put(user.getChatId(), true);
        registry.clear(user);
        return String.format(Locale.US, """
                        %s <b>%s</b>

                        %s <b>%s</b>
                        <blockquote>• %s: <b>$%,.2f</b>
                        • %s: <b>$%,.2f</b>
                        • %s: <b>$%,.2f</b></blockquote>
                        """,
                DynamicEmoji.SUCCESS.getEmoji(), translations.get("reward-transferred"),
                DynamicEmoji.MONEY.getEmoji(), translations.get("result"),
                translations.get("transferred"), operation.getAmount(),
                translations.get("service-balance"), operation.getBalance(),
                translations.get("referral-balance"), operation.getReferralBalance()
        );
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, RefTransferProceedMenu option) {
        return switch (option) {
            case RETRY -> UserMenu.REF_TRANSFER_PROCEED;
            case RETURN -> UserMenu.REF;
        };
    }

    @Override
    public Map<RefTransferProceedMenu, Predicate<UserData>> getVisibilityPredicates(UserData userData) {
        return Map.of(
                RefTransferProceedMenu.RETRY,
                user -> !completed.getOrDefault(user.getChatId(), false)
        );
    }
}
