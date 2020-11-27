package com.kraaiennest.kraaiennestapp.api;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class LocalDateTimeConverter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Timestamp format
        // "2012-06-11T20:06:10Z" format
        LocalDateTime time = LocalDateTime.parse(json.getAsString());

        return time;
    }

    /**
     * This never seems to be called. Probably a GSON bug.
     */
    @Override
    public JsonElement serialize(LocalDateTime time, Type typeOfT, JsonSerializationContext context) {
        return new JsonPrimitive(time.toString());
    }
}
