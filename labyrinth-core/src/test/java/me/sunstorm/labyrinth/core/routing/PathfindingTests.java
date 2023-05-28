package me.sunstorm.labyrinth.core.routing;

import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.entity.Monster;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.level.LevelStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PathfindingTests {
    private LevelStore store;
    private Level emptyLevel;

    @BeforeEach
    void setup() {
        store = new LevelStore(Path.of(""));
        emptyLevel = store.createEmpty();
        emptyLevel.calculateNeighbours();
    }

    public static Stream<Arguments> providePathArguments() {
        return Stream.of(
                Arguments.of(Position.of(0, 0), Position.of(5, 0), List.of(Position.of(1, 0), Position.of(2, 0))),
                Arguments.of(Position.of(0, 0), Position.of(0, 5), List.of(Position.of(0, 1), Position.of(0, 2))),
                Arguments.of(Position.of(5, 5), Position.of(0, 0), List.of(Position.of(4, 5), Position.of(3, 5)))
                // todo: add more test cases
        );
    }

    @ParameterizedTest
    @MethodSource("providePathArguments")
    void testPathfinding(Position from, Position to, List<Position> expected) {
        var monster = new Monster(from);
        var result = monster.nextMove(emptyLevel, to);
        assertThat(result).containsExactlyElementsOf(expected);
    }
}
