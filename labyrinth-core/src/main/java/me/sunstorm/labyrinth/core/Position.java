package me.sunstorm.labyrinth.core;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a coordinate on the game grid.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Position {
    /**
     * A constant representing the (0, 0) {@link Position}.
     */
    public static final Position ZERO = Position.of(0, 0);

    /**
     * A constant representing the maximum {@link Position} available in the game grid. This is calculated using {@link Constants#LEVEL_SIZE}.
     */
    public static final Position MAP_EDGE = Position.of(Constants.LEVEL_SIZE - 1, Constants.LEVEL_SIZE - 1);

    private final int x;
    private final int y;

    /**
     * Creates a {@link Position} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return The created position.
     */
    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    /**
     *  Calculates the Euclidean distance to the specified {@link Position}.
     *
     * @param other the other position
     * @return The calculated distance.
     */
    public double distance(Position other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Calculates the squared distance to the specified {@link Position}.
     *
     * @param other the other position
     * @return The squared distance.
     */
    public double distanceSquared(Position other) {
        if (this.equals(other)) return 0;
        return Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2);
    }

    /**
     * Calculates the Manhattan distance to the specified {@link Position}.
     *
     * @param other the other position
     * @return The Manhattan distance.
     */
    public double manhattanDistance(Position other) {
        return Math.abs(x - other.x) + Math.abs(y + other.y);
    }

    /**
     * Performs an addition between {@code this} and the specified {@link Position}.
     *
     * @param other the other position
     * @return The resulting {@link Position}.
     */
    public Position add(Position other) {
        return Position.of(x + other.x, y + other.y);
    }

    /**
     * Performs a subtraction between {@code this} and the specified {@link Position}.
     *
     * @param other the other position
     * @return The resulting {@link Position}.
     */
    public Position subtract(Position other) {
        return Position.of(x - other.x, y - other.y);
    }

    /**
     * Calculates the multiplied values of the coordinates.
     *
     * @param n multiplication factor
     * @return The resulting {@link Position}.
     */
    public Position multiply(int n) {
        return Position.of(x * n, y * n);
    }

    /**
     * Calculates whether this {@link Position} is inside the defined bounding box.
     * Coordinates are checked inclusively.
     *
     * @param a The lower corner of the bounding box
     * @param b The higher corner of the bounding box
     * @return {@code true} when this {@link Position} is inside the bounding box.
     */
    public boolean isBoundedBy(Position a, Position b) {
        return x >= a.x && x <= b.x && y >= a.y && y <= b.y;
    }

    /**
     * Converts this {@link Position} to {@link String}.
     *
      * @return the string representation of this {@link Position}
     */
    @Override
    public String toString() {
        return x + " " + y;
    }
}
