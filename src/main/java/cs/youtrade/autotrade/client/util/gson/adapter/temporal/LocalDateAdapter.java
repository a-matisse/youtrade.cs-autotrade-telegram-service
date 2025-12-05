package cs.youtrade.autotrade.client.util.gson.adapter.temporal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends ClassicTemporalAccessorAdapter<LocalDate> {
    @Override
    protected DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ISO_LOCAL_DATE;
    }

    @Override
    protected LocalDate parseString(String str) {
        return LocalDate.parse(str, getFormatter());
    }
}
