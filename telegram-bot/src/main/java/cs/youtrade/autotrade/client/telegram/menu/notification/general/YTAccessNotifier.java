package cs.youtrade.autotrade.client.telegram.menu.notification.general;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.notification.YTAccessNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class YTAccessNotifier extends YTTextNotifier<YTAccessNotification> {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.forLanguageTag("ru"));

    public YTAccessNotifier(UserTextMessageSender sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTAccessNotification data) {
        String reason = escapeHtml(data.getReason());
        String until = formatUntil(data.getUntil());
        return switch (data.getType()) {
            case USER_BLOCKED -> String.format("""
                    🛑 <b>Обслуживание приостановлено</b>

                    <blockquote>• До: <b>%s</b>
                    • Причина: %s</blockquote>

                    Баланс и кабинет доступны в /start. Если нужна помощь, <a href="https://t.me/youtradecs_sup">напишите в поддержку</a>.""", until, reason);
            case USER_UNBLOCKED -> String.format("""
                    ✅ <b>Обслуживание восстановлено</b>

                    <blockquote>• Причина: %s</blockquote>

                    Можно продолжать работу в /start.""", reason);
            case BARGAIN_ACCESS_GRANTED -> String.format("""
                    🔥 <b>Доступ к Y.CS Bargain™ открыт</b>

                    <blockquote>• До: <b>%s</b>
                    • Основание: %s</blockquote>

                    Чтобы торговаться, включите режим самостоятельно в /deep.""", until, reason);
            case BARGAIN_ACCESS_REVOKED -> String.format("""
                    🔒 <b>Доступ к Y.CS Bargain™ завершился</b>

                    <blockquote>• Причина: %s</blockquote>

                    Обычная автопокупка остаётся доступной. Условия продления — в /deep.""", reason);
            default -> throw new IllegalArgumentException("Unsupported access notification: " + data.getType());
        };
    }

    private static String formatUntil(String value) {
        if (value == null || value.isBlank()) return "не указан";
        try {
            return LocalDateTime.parse(value).format(DATE_FORMAT) + " UTC";
        } catch (java.time.format.DateTimeParseException e) {
            return escapeHtml(value);
        }
    }

    private static String escapeHtml(String value) {
        if (value == null || value.isBlank()) return "не указана";
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
