package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.HistoryDateTimeFormat;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class PurchaseFreezePeriodState extends YTPTextMenuState<PurchaseFreezeMenu> {
    private final PurchaseFreezeRegistry registry;
    private final AccountsV2Endpoint endpoint;
    private final YTPPageProcessor pageProcessor;

    public PurchaseFreezePeriodState(UserTextMessageSender sender, PurchaseFreezeRegistry registry,
                                     AccountsV2Endpoint endpoint, YTPPageProcessor pageProcessor) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
        this.pageProcessor = pageProcessor;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_PURCHASE_FREEZE_PERIOD; }

    @Override
    public PurchaseFreezeMenu getOption(String value) { return PurchaseFreezeMenu.valueOf(value); }

    @Override
    public PurchaseFreezeMenu[] getOptions(UserData user) {
        return PurchaseFreezeMenu.values();
    }

    @Override
    public Map<PurchaseFreezeMenu, Predicate<UserData>> getVisibilityPredicates(UserData user) {
        return Map.of(PurchaseFreezeMenu.RESUME, u -> {
            var account = registry.getOrCreate(u, PurchaseFreezeData::new).getAccount();
            return account != null && account.getBuyBalance() != null
                    && Boolean.TRUE.equals(account.getBuyBalance().getUserPurchaseFrozen());
        });
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, PurchaseFreezeMenu option) {
        if (option == PurchaseFreezeMenu.BACK) {
            registry.get(user);
            return UserMenu.ACCOUNTS_MORE;
        }
        int days = switch (option) {
            case ONE_DAY -> 1;
            case SEVEN_DAYS -> 7;
            case THIRTY_DAYS -> 30;
            case RESUME -> 0;
            case BACK -> throw new IllegalStateException();
        };
        updateFreeze(user, registry.getOrCreate(user, PurchaseFreezeData::new), days);
        return supportedState();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.getOrCreate(user, PurchaseFreezeData::new);
        refreshAccount(user, data);
        var account = data.getAccount();
        String name = account == null || account.getGivenName() == null ? "" : String.format("""
                • Имя: <b>%s</b>
                """, escape(account.getGivenName()));
        String purchases = "• Состояние пока недоступно";
        if (account != null && account.getBuyBalance() != null) {
            var balance = account.getBuyBalance();
            String userPause = Boolean.TRUE.equals(balance.getUserPurchaseFrozen())
                    ? "до " + date(balance.getUserPurchaseUnfreezeAt()) : "нет";
            String systemPause = Boolean.TRUE.equals(balance.getSystemPurchaseFrozen())
                    ? "до " + date(balance.getAccountUnfreezeAt()) : "нет";
            purchases = String.format("""
                    • Ваша пауза: <b>%s</b>
                    • Системная пауза: <b>%s</b>
                    """, userPause, systemPause).stripTrailing();
        }
        return String.format("""
                %s <i>Пауза покупок</i>

                %s <b>Аккаунт</b>
                <blockquote>• ID: <code>%s</code>
                %s</blockquote>

                %s <b>Состояние покупок</b>
                <blockquote>%s</blockquote>

                %s%s <b>Выберите срок или отправьте число дней</b>
                └ <i>От 1 до 3650 дней. Новый срок считается с момента выбора.</i>
                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.STEAM.getEmoji(),
                data.getTokenId(), name.stripTrailing(), DynamicEmoji.ITEM_RECEIVE.getEmoji(),
                purchases, resultText(data), DynamicEmoji.WRITE.getEmoji());
    }

    private String resultText(PurchaseFreezeData data) {
        if (data.getResponseStatus() == null) return "";
        String result = switch (data.getResponseStatus()) {
            case 204 -> data.getRequestedDays() == 0
                    ? String.format("""
                            %s <b>Ваша пауза снята</b>
                            """, DynamicEmoji.SUCCESS.getEmoji())
                    : String.format("""
                            %s <b>Покупки приостановлены на %s %s</b>
                            """, DynamicEmoji.SUCCESS.getEmoji(), data.getRequestedDays(),
                            pluralDays(data.getRequestedDays()));
            case 404 -> "Аккаунт покупки не найден или больше вам не принадлежит.";
            case 400 -> "Не удалось установить выбранный срок.";
            default -> "Не удалось изменить состояние покупок. Попробуйте позже.";
        };
        boolean systemPause = data.getResponseStatus() == 204 && data.getRequestedDays() == 0
                && data.getAccount() != null && data.getAccount().getBuyBalance() != null
                && Boolean.TRUE.equals(data.getAccount().getBuyBalance().getSystemPurchaseFrozen());
        String notice = systemPause ? "└ <i>Системная пауза продолжает действовать.</i>" : "";
        return String.format("""
                %s
                %s

                """, result.stripTrailing(), notice);
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        long days;
        try {
            days = Long.parseLong(update.getMessage().getText().trim());
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, "Введите число дней от 1 до 3650.");
            return supportedState();
        }
        if (days < 1 || days > 3650) {
            sender.sendTextMes(bot, user, "Введите число дней от 1 до 3650.");
        } else updateFreeze(user, registry.getOrCreate(user, PurchaseFreezeData::new), (int) days);
        return supportedState();
    }

    private void updateFreeze(UserData user, PurchaseFreezeData data, int days) {
        data.setRequestedDays(days);
        try {
            var answer = endpoint.freezeBuyerPurchases(user.getChatId(), data.getTokenId(), days);
            data.setResponseStatus(answer.getStatus());
            if (answer.getStatus() == 204) refreshAccount(user, data);
        } catch (RuntimeException error) {
            data.setResponseStatus(0);
        }
    }

    private void refreshAccount(UserData user, PurchaseFreezeData data) {
        try {
            data.setAccount(pageProcessor.getPage(user.getChatId()).fcd().getAccounts().stream()
                    .filter(item -> item.getId().equals(data.getTokenId())).findFirst().orElse(null));
        } catch (RuntimeException ignored) { }
    }

    private String date(String value) {
        if (value == null) return "неизвестного времени";
        try {
            return HistoryDateTimeFormat.display(value);
        } catch (DateTimeParseException error) {
            return "неизвестного времени";
        }
    }

    private String pluralDays(int days) {
        if (days % 100 >= 11 && days % 100 <= 14) return "дней";
        return switch (days % 10) {
            case 1 -> "день";
            case 2, 3, 4 -> "дня";
            default -> "дней";
        };
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
