package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
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
                                     AccountsV2Endpoint endpoint, YTPPageProcessor pageProcessor, BotTexts botTexts) {
        super(sender, botTexts);
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
        var translations = botTexts.forUser(user, "accounts.PurchaseFreeze");
        var data = registry.getOrCreate(user, PurchaseFreezeData::new);
        refreshAccount(user, data);
        var account = data.getAccount();
        String name = account == null || account.getGivenName() == null ? "" : String.format("""
                • %s: <b>%s</b>
                """,
                translations.get("name"),
                escape(account.getGivenName()));
        String purchases = String.format("• %s",
                translations.get("status-is-not-available-yet"));
        if (account != null && account.getBuyBalance() != null) {
            var balance = account.getBuyBalance();
            String userPause = Boolean.TRUE.equals(balance.getUserPurchaseFrozen())
                    ? String.format("%s ",
                            translations.get("until")) + date(balance.getUserPurchaseUnfreezeAt(), translations) : translations.get("none");
            String systemPause = Boolean.TRUE.equals(balance.getSystemPurchaseFrozen())
                    ? String.format("%s ",
                            translations.get("until")) + date(balance.getAccountUnfreezeAt(), translations) : translations.get("none");
            purchases = String.format("""
                    • %s: <b>%s</b>
                    • %s: <b>%s</b>
                    """,
                    translations.get("your-pause"),
                    userPause,
                    translations.get("system-pause"),
                    systemPause).stripTrailing();
        }
        return String.format("""
                %s <i>%s</i>

                %s <b>%s</b>
                <blockquote>• ID: <code>%s</code>
                %s</blockquote>

                %s <b>%s</b>
                <blockquote>%s</blockquote>

                %s%s <b>%s</b>
                └ <i>%s</i>
                """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                translations.get("purchase-pause"),
                DynamicEmoji.STEAM.getEmoji(),
                translations.get("account"),
                data.getTokenId(),
                name.stripTrailing(),
                DynamicEmoji.ITEM_RECEIVE.getEmoji(),
                translations.get("purchase-status"),
                purchases,
                resultText(data, translations),
                DynamicEmoji.WRITE.getEmoji(),
                translations.get("choose-a-duration-or-send-a-number"),
                translations.get("from-1-to-3-650-days-the"));
    }

    private String resultText(PurchaseFreezeData data, LocalizedTexts translations) {
        if (data.getResponseStatus() == null) return "";
        String result = switch (data.getResponseStatus()) {
            case 204 -> data.getRequestedDays() == 0
                    ? String.format("""
                            %s <b>%s</b>
                            """,
                            DynamicEmoji.SUCCESS.getEmoji(),
                            translations.get("your-pause-has-been-removed"))
                    : String.format("""
                            %s <b>%s</b>
                            """,
                            DynamicEmoji.SUCCESS.getEmoji(),
                            translations.format("purchases-paused-for", Map.of("days", data.getRequestedDays())));
            case 404 -> translations.get("the-buying-account-was-not-found-or");
            case 400 -> translations.get("could-not-set-the-selected-duration");
            default -> translations.get("could-not-change-the-purchase-status-try");
        };
        boolean systemPause = data.getResponseStatus() == 204 && data.getRequestedDays() == 0
                && data.getAccount() != null && data.getAccount().getBuyBalance() != null
                && Boolean.TRUE.equals(data.getAccount().getBuyBalance().getSystemPurchaseFrozen());
        String notice = systemPause ? String.format("└ <i>%s</i>",
                translations.get("the-system-pause-is-still-active")) : "";
        return String.format("""
                %s
                %s

                """, result.stripTrailing(), notice);
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        var translations = botTexts.forUser(user, "accounts.PurchaseFreeze");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, translations.get("send-the-value-as-a-text-message"));
            return supportedState();
        }
        long days;
        try {
            days = Long.parseLong(update.getMessage().getText().trim());
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, translations.get("enter-a-number-of-days-from-1"));
            return supportedState();
        }
        if (days < 1 || days > 3650) {
            sender.sendTextMes(bot, user, translations.get("enter-a-number-of-days-from-1"));
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

    private String date(String value, LocalizedTexts translations) {
        if (value == null) return translations.get("an-unknown-time");
        try {
            return HistoryDateTimeFormat.display(value);
        } catch (DateTimeParseException error) {
            return translations.get("an-unknown-time");
        }
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
