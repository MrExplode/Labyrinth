package me.sunstorm.labyrinth.core.tile;

import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.level.LevelStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TileWallTests {
    private static LevelStore store;
    private static Level level;

    @BeforeAll
    static void setup() {
        store = new LevelStore(Path.of(""));
        level = store.createEmpty();
    }

    private static Stream<Arguments> provideEdgeParameters() {
        return Stream.of(
                Arguments.of(Position.of(3, 0), Direction.UP, false),
                Arguments.of(Position.of(3, 0), Direction.UP, true),
                Arguments.of(Position.of(3, 5), Direction.DOWN, false),
                Arguments.of(Position.of(3, 5), Direction.DOWN, true),
                Arguments.of(Position.of(0, 3), Direction.LEFT, false),
                Arguments.of(Position.of(0, 3), Direction.LEFT, true),
                Arguments.of(Position.of(5, 3), Direction.RIGHT, false),
                Arguments.of(Position.of(5, 3), Direction.RIGHT, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideEdgeParameters")
    void testEdgeWall(Position pos, Direction d, boolean contains) {
        var tile = level.get(pos);
        tile.setWall(level, d);
        assertThat(tile.getWalls().contains(d)).isEqualTo(contains);
    }

    @Test
    void testWallDuplicationNone() {
        var tile = level.get(3, 3);
        tile.setWall(level, Direction.RIGHT);
        assertThat(tile.getWalls().contains(Direction.RIGHT)).isTrue();
        assertThat(level.get(4, 3).getWalls().contains(Direction.LEFT)).isFalse();
    }

    @Test
    void testWallDuplicationSelf() {
        var tile = level.get(3, 3);
        tile.getWalls().add(Direction.LEFT);
        tile.setWall(level, Direction.LEFT);
        assertThat(tile.getWalls().contains(Direction.LEFT)).isFalse();
        assertThat(level.get(2, 3).getWalls().contains(Direction.RIGHT)).isFalse();
    }

    @Test
    void testWallDuplicationOther() {
        var tile = level.get(3, 3);
        var other = level.get(3, 2);
        other.getWalls().add(Direction.DOWN);
        tile.setWall(level, Direction.UP);
        assertThat(tile.getWalls().contains(Direction.DOWN)).isFalse();
        assertThat(other.getWalls().contains(Direction.DOWN)).isFalse();
    }

    @Test
    void testWallDuplicationBoth() {
        var tile = level.get(3, 3);
        var other = level.get(3, 4);
        tile.getWalls().add(Direction.DOWN);
        other.getWalls().add(Direction.UP);
        tile.setWall(level, Direction.DOWN);
        assertThat(tile.getWalls().contains(Direction.DOWN)).isFalse();
        assertThat(other.getWalls().contains(Direction.DOWN)).isFalse();
    }
}
