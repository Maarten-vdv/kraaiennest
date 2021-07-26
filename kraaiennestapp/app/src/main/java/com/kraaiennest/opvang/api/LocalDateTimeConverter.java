package com.kraaiennest.opvang.api;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        // Timestamp format
        // "2012-06-11T20:06:10Z" format

        try {
            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * This never seems to be called. Probably a GSON bug.
     */
    @Override
    public JsonElement serialize(LocalDateTime time, Type typeOfT, JsonSerializationContext context) {
        return new JsonPrimitive(time.toString());
    }
}
