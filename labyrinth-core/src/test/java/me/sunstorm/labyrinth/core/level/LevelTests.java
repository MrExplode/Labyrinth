package me.sunstorm.labyrinth.core.level;

import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelTests {
    private Level level;

    @BeforeEach
    void setup() {
        level = new LevelStore(Path.of("")).createEmpty();
    }

    @Test
    void testLevelCopy() {
        var copy = level.copy();
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                assertThat(copy.get(x, y)).isEqualTo(level.get(x, y));
            }
        }
        assertThat(copy.getName()).isEqualTo(level.getName());
        assertThat(copy.getPlayer()).isEqualTo(level.getPlayer());
        assertThat(copy.getMonster()).isEqualTo(level.getMonster());
    }

    @Test
    void testIterator() {
        Iterator<Tile> iterator = level.iterator();
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                assertThat(iterator.next()).isEqualTo(level.get(x, y));
            }
        }
    }

    private static Stream<Arguments> provideExitParameters() {
        return Stream.of(
            Arguments.of(Position.of(0, 0), List.of(Direction.UP), true),
            Arguments.of(Position.of(0, 0), List.of(), false),
            Arguments.of(Position.of(5, 3), List.of(Direction.RIGHT), true),
            Arguments.of(Position.of(5, 5), List.of(Direction.DOWN), true),
            Arguments.of(Position.of(0, 5), List.of(Direction.LEFT), true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideExitParameters")
    void testExitDetection(Position pos, List<Direction> mutations, boolean expected) {
        mutations.forEach(d -> level.get(pos).setWall(level, d));
        assertThat(level.isExitTile(pos)).isEqualTo(expected);
    }
}
