package cs.youtrade.autotrade.client.util.autotrade.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class HistoryDateTimeFormat {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.forLanguageTag("ru"));

    private HistoryDateTimeFormat() {
    }

    public static LocalDateTime parse(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String display(String value) {
        return parse(value).format(DISPLAY_FORMATTER);
    }
}
