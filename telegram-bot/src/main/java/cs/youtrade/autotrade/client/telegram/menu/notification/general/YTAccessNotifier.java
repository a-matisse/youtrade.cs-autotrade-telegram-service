package cs.youtrade.autotrade.client.telegram.menu.notification.general;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.notification.YTAccessNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class YTAccessNotifier extends YTTextNotifier<YTAccessNotification> {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.forLanguageTag("ru"));

    public YTAccessNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTAccessNotification data) {
        var translations = botTexts.forUser(user, "notification.AccessNotifications");
        String reason = escapeHtml(data.getReason(), translations);
        String until = formatUntil(data.getUntil(), translations);
        return switch (data.getType()) {
            case USER_BLOCKED -> String.format("""
                    🛑 <b>%s</b>

                    <blockquote>• %s: <b>%s</b>
                    • %s: %s</blockquote>

                    %s""",
                    translations.get("service-suspended"),
                    translations.get("until"),
                    until,
                    translations.get("reason"),
                    reason,
                    translations.format("your-balance-and-dashboard-are-available-in", Map.of("value1", String.format("<a href=\"https://t.me/youtradecs_sup\">%s</a>", translations.get("contact-support")))));
            case USER_UNBLOCKED -> String.format("""
                    ✅ <b>%s</b>

                    <blockquote>• %s: %s</blockquote>

                    %s""",
                    translations.get("service-restored"),
                    translations.get("reason"),
                    reason,
                    translations.get("you-can-continue-in-start"));
            case BARGAIN_ACCESS_GRANTED -> String.format("""
                    🔥 <b>%s</b>

                    <blockquote>• %s: <b>%s</b>
                    • %s: %s</blockquote>

                    %s""",
                    translations.get("y-cs-bargain-access-granted"),
                    translations.get("until"),
                    until,
                    translations.get("basis"),
                    reason,
                    translations.get("to-start-bargaining-enable-the-mode-yourself"));
            case BARGAIN_ACCESS_REVOKED -> String.format("""
                    🔒 <b>%s</b>

                    <blockquote>• %s: %s</blockquote>

                    %s""",
                    translations.get("y-cs-bargain-access-expired"),
                    translations.get("reason"),
                    reason,
                    translations.get("standard-automatic-buying-remains-available-see-deep"));
            default -> throw new IllegalArgumentException("Unsupported access notification: " + data.getType());
        };
    }

    private static String formatUntil(String value, LocalizedTexts translations) {
        if (value == null || value.isBlank()) return translations.get("not-specified-2");
        try {
            return LocalDateTime.parse(value).format(DATE_FORMAT) + " UTC";
        } catch (java.time.format.DateTimeParseException e) {
            return escapeHtml(value, translations);
        }
    }

    private static String escapeHtml(String value, LocalizedTexts translations) {
        if (value == null || value.isBlank()) return translations.get("not-specified");
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
