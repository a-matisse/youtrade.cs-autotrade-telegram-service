package cs.youtrade.autotrade.client.util.communication.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs.youtrade.autotrade.client.util.communication.gson.adapter.LocalDateAdapter;
import cs.youtrade.autotrade.client.util.communication.gson.adapter.LocalDateTimeAdapter;
import cs.youtrade.autotrade.client.util.communication.gson.adapter.LocalTimeAdapter;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class GsonConfig {
    private static final Map<Type, Object> adapters = new HashMap<>();

    static {
        addAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        addAdapter(LocalDate.class, new LocalDateAdapter());
        addAdapter(LocalTime.class, new LocalTimeAdapter());
    }

    public static Gson createGson() {
        var builder = new GsonBuilder();
        adapters.forEach(builder::registerTypeAdapter);
        return builder.create();
    }

    public static <T> void addAdapter(Class<T> type, Object adapter) {
        adapters.put(type, adapter);
    }
}
