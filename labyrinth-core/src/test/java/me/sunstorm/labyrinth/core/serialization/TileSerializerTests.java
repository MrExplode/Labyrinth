package me.sunstorm.labyrinth.core.serialization;

import me.sunstorm.labyrinth.core.Constants;
import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.*;

public class TileSerializerTests {

    @Test
    void testTileSerialization() {
        var tile = new Tile(EnumSet.of(Direction.UP, Direction.DOWN), Position.of(3, 5));
        var tileSerialized = Constants.GSON.toJson(tile);
        var excepted = """
                {
                  "position": {
                    "x": 3,
                    "y": 5
                  },
                  "walls": 12
                }""";
        assertThat(tileSerialized).isEqualTo(excepted);
    }

    @Test
    void testTileDeserialization() {
        var from = """
                {
                  "position": {
                    "x": 4,
                    "y": 2
                  },
                  "walls": 7
                }
                """;
        var deserialized = Constants.GSON.fromJson(from, Tile.class);
        var excepted = new Tile(EnumSet.of(Direction.UP, Direction.LEFT, Direction.RIGHT), Position.of(4, 2));
        assertThat(deserialized).isEqualTo(excepted);
    }
}
