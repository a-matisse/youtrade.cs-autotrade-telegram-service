package cs.youtrade.autotrade.client.util.gson;

import com.google.gson.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UpdateTypeAdapter implements JsonDeserializer<Update> {

    private static final Set<String> IGNORED_FIELDS = Set.of(
            "maybe_inaccessible_message",
            "maybeInaccessibleMessage",
            "old_chat_member",
            "oldChatMember",
            "new_chat_member",
            "newChatMember"
    );

    private final Gson defaultGson;

    public UpdateTypeAdapter() {
        this.defaultGson = new GsonBuilder()
                .registerTypeAdapter(MaybeInaccessibleMessage.class,
                        (JsonDeserializer<MaybeInaccessibleMessage>) (json, typeOfT, context) ->
                                context.deserialize(json, Message.class))
                .create();
    }

    @Override
    public Update deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
    ) throws JsonParseException {
        if (json == null || json.isJsonNull())
            return null;

        removeProblematicFieldsInPlace(json);
        return defaultGson.fromJson(json, Update.class);
    }

    private void removeProblematicFieldsInPlace(
            JsonElement el
    ) {
        if (el == null || el.isJsonNull())
            return;

        if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();
            Iterator<Map.Entry<String, JsonElement>> it = obj.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, JsonElement> entry = it.next();
                String key = entry.getKey();
                // Remove interface-typed fields which this service does not consume.
                if (IGNORED_FIELDS.contains(key))
                    it.remove();
                else
                    removeProblematicFieldsInPlace(entry.getValue());
            }
        } else if (el.isJsonArray()) {
            for (JsonElement item : el.getAsJsonArray())
                removeProblematicFieldsInPlace(item);
        }
    }
}
