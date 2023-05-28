package me.sunstorm.labyrinth.core.serialization;

import com.google.gson.*;
import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link Gson} serializer / deserializer for the {@link Tile} type.
 */
public class TileSerializer implements JsonSerializer<Tile>, JsonDeserializer<Tile> {

    @Override
    public Tile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Position position = context.deserialize(object.get("position"), Position.class);
        var walls = Direction.from(object.get("walls").getAsInt());
        return new Tile(walls, position);
    }

    @Override
    public JsonElement serialize(Tile src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("position", context.serialize(src.getPosition()));
        var walls = new AtomicInteger();
        src.getWalls().forEach(w -> walls.updateAndGet(v -> v | w.getFlag()));
        result.addProperty("walls", walls.get());
        return result;
    }
}
