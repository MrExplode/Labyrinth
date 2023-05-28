package me.sunstorm.labyrinth.core.tile;

import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.level.LevelStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class TileEdgeTests {
    private Level level;

    @BeforeEach
    void setup() {
        level = new LevelStore(Path.of("")).createEmpty();
    }

    private static Stream<Arguments> provideEdges() {
        return Stream.of(
                Arguments.of(Position.of(0, 0), true),
                Arguments.of(Position.of(5, 0), true),
                Arguments.of(Position.of(0, 5), true),
                Arguments.of(Position.of(5, 5), true),
                Arguments.of(Position.of(5, 3), true),
                Arguments.of(Position.of(3, 0), true),
                Arguments.of(Position.of(3, 3), false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideEdges")
    void testEdgeDetection(Position pos, boolean edge) {
        assertThat(level.get(pos).isEdge()).isEqualTo(edge);
    }

    private static Stream<Arguments> provideEdgeDirections() {
        return Stream.of(
                Arguments.of(Position.of(0, 0), List.of(Direction.UP, Direction.LEFT)),
                Arguments.of(Position.of(5, 0), List.of(Direction.UP, Direction.RIGHT)),
                Arguments.of(Position.of(0, 5), List.of(Direction.DOWN, Direction.LEFT)),
                Arguments.of(Position.of(5, 5), List.of(Direction.DOWN, Direction.RIGHT)),
                Arguments.of(Position.of(3, 3), List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("provideEdgeDirections")
    void testEdgeSides(Position pos, List<Direction> expected) {
        assertThat(level.get(pos).edgeSides()).containsExactlyInAnyOrderElementsOf(expected);
    }
}
