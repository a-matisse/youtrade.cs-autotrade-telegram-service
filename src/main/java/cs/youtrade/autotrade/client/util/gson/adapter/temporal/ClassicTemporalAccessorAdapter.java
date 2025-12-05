package cs.youtrade.autotrade.client.util.gson.adapter.temporal;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public abstract class ClassicTemporalAccessorAdapter<T extends Temporal> extends TypeAdapter<T> {
    protected abstract DateTimeFormatter getFormatter();
    protected abstract T parseString(String str);

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(getFormatter().format(value));
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String str = in.nextString();
        return parseString(str);
    }
}
