package me.sunstorm.labyrinth.core.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * {@link Gson} serializer / deserializer for the {@link LocalDateTime} type.
 */
public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(json.getAsLong()), ZoneId.systemDefault());
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
}
