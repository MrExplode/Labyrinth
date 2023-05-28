package me.sunstorm.labyrinth.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the possible directions in the grid.
 */
@Getter
@RequiredArgsConstructor
public enum Direction {
    /**
     * Direction of <i><b>left</b></i>.
     */
    LEFT(1),

    /**
     * Direction of <i><b>right</b></i>.
     */
    RIGHT(2),

    /**
     * Direction of <i><b>up</b></i>.
     */
    UP(4),

    /**
     * Direction of <i><b>down</b></i>.
     */
    DOWN(8);

    private final int flag;

    /**
     * An array caching all values for {@link Direction} to avoid cloning the backing array every call to {@link Direction#values()}.
     */
    public static final Direction[] VALUES = values();

    /**
     * A list containing the possible directions in CSS order. (e.g. border width)
     */
    public static final List<Direction> CSS_ORDER = List.of(UP, RIGHT, DOWN, LEFT);

    /**
     * Deserializes an integer flag into a set of {@link Direction}.
     * @param flag The encoded integer value
     * @return A set of the encoded directions.
     */
    public static Set<Direction> from(int flag) {
        EnumSet<Direction> result = EnumSet.noneOf(Direction.class);
        for (Direction d : VALUES) {
            if ((flag & d.flag) > 0) result.add(d);
        }
        return result;
    }

    /**
     * Calculates the normal vector of this direction.
     *
     * @return the {@link Position} representing the normal vector
     */
    public Position asOffset() {
        return switch (this) {
            case LEFT -> Position.of(-1, 0);
            case RIGHT -> Position.of(1, 0);
            case UP -> Position.of(0, -1);
            case DOWN -> Position.of(0, 1);
        };
    }

    /**
     * @return The opposite of this {@link Direction}.
     */
    public Direction opposite() {
        return switch (this) {
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case UP -> DOWN;
            case DOWN -> UP;
        };
    }
}
