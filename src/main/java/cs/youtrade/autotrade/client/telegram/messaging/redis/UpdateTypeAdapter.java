package cs.youtrade.autotrade.client.telegram.messaging.redis;

import com.google.gson.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class UpdateTypeAdapter implements JsonDeserializer<Update> {

    private final Gson defaultGson;

    public UpdateTypeAdapter() {
        // Gson с десериализатором для интерфейса MaybeInaccessibleMessage -> Message
        this.defaultGson = new GsonBuilder()
                .registerTypeAdapter(MaybeInaccessibleMessage.class, new JsonDeserializer<MaybeInaccessibleMessage>() {
                    @Override
                    public MaybeInaccessibleMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        // десериализуем в конкретный класс Message
                        return context.deserialize(json, Message.class);
                    }
                })
                .create();
    }

    @Override
    public Update deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;

        // Удаляем проблемные поля в исходном дереве (in-place), чтобы не копировать весь JSON.
        removeProblematicFieldsInPlace(json);

        // Затем десериализуем очищенное дерево в Update
        return defaultGson.fromJson(json, Update.class);
    }

    private void removeProblematicFieldsInPlace(JsonElement el) {
        if (el == null || el.isJsonNull()) return;

        if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();
            Iterator<Map.Entry<String, JsonElement>> it = obj.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, JsonElement> entry = it.next();
                String key = entry.getKey();
                // удаляем оба варианта имени поля
                if ("maybe_inaccessible_message".equals(key) || "maybeInaccessibleMessage".equals(key)) {
                    it.remove();
                } else {
                    removeProblematicFieldsInPlace(entry.getValue());
                }
            }
        } else if (el.isJsonArray()) {
            for (JsonElement item : el.getAsJsonArray()) {
                removeProblematicFieldsInPlace(item);
            }
        }
    }
}
