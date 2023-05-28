package me.sunstorm.labyrinth.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PositionTests {

    private static Stream<Arguments> provideDistanceArguments() {
        return Stream.of(
                Arguments.of(Position.of(0, 0), Position.of(0, 5), 5d, 25d, 5d),
                Arguments.of(Position.of(0, 0), Position.of(5, 0), 5d, 25d, 5d),
                Arguments.of(Position.of(0, 0), Position.of(5, 5), Math.sqrt(50d), 50d, 10d),
                Arguments.of(Position.of(0, 5), Position.of(5, 0), Math.sqrt(50d), 50d, 10d),
                Arguments.of(Position.of(0, 0), Position.of(0, 0), 0d, 0d, 0d)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDistanceArguments")
    void testDistance(Position pos1, Position pos2, double distance, double squared, double manhattan) {
        assertThat(pos1.distance(pos2)).isEqualTo(distance);
        assertThat(pos1.distanceSquared(pos2)).isEqualTo(squared);
        assertThat(pos1.manhattanDistance(pos2)).isEqualTo(manhattan);
    }

    private static Stream<Arguments> provideAdditionArguments() {
        return Stream.of(
            Arguments.of(Position.of(0, 0), Position.of(0, 5), Position.of(0, 5)),
            Arguments.of(Position.of(3, 2), Position.of(1, 1), Position.of(4, 3)),
            Arguments.of(Position.of(5, 5), Position.of(-2, 3), Position.of(3, 8)),
            Arguments.of(Position.of(3, 3), Position.of(2, -2), Position.of(5, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("provideAdditionArguments")
    void testAddition(Position pos1, Position pos2, Position expected) {
        assertThat(pos1.add(pos2)).isEqualTo(expected);
    }

    @Test
    void testMultiplication() {
        var pos1 = Position.of(1, 1);
        assertThat(pos1.multiply(2)).isEqualTo(Position.of(2, 2));
        assertThat(pos1.multiply(-2)).isEqualTo(Position.of(-2, -2));
        assertThat(pos1.multiply(5)).isEqualTo(Position.of(5, 5));
    }

    @Test
    void testSubtraction() {
        var pos1 = Position.of(1, 1);
        assertThat(pos1.subtract(Position.of(0, 1))).isEqualTo(Position.of(1, 0));
        assertThat(pos1.subtract(Position.of(-1, -1))).isEqualTo(Position.of(2, 2));
        assertThat(pos1.subtract(Position.of(1, 1))).isEqualTo(Position.of(0, 0));
    }

    private static Stream<Arguments> provideBoundaryArguments() {
        return Stream.of(
            Arguments.of(Position.of(0, 0), Position.ZERO, Position.MAP_EDGE, true),
            Arguments.of(Position.of(5, 5), Position.ZERO, Position.MAP_EDGE, true),
            Arguments.of(Position.of(-1, 0), Position.ZERO, Position.MAP_EDGE, false),
            Arguments.of(Position.of(0, -1), Position.ZERO, Position.MAP_EDGE, false),
            Arguments.of(Position.of(5, 6), Position.ZERO, Position.MAP_EDGE, false),
            Arguments.of(Position.of(6, 5), Position.ZERO, Position.MAP_EDGE, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideBoundaryArguments")
    void testBoundary(Position pos, Position a, Position b, boolean expected) {
        assertThat(pos.isBoundedBy(a, b)).isEqualTo(expected);
    }
}
