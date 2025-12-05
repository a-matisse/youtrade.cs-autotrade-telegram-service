package cs.youtrade.autotrade.client.util.gson.adapter.temporal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends ClassicTemporalAccessorAdapter<LocalDateTime> {
    @Override
    protected DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    @Override
    protected LocalDateTime parseString(String str) {
        return LocalDateTime.parse(str, getFormatter());
    }
}
