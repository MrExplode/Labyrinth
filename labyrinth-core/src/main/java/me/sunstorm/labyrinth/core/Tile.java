package me.sunstorm.labyrinth.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a tile in the game {@link Level}.
 */
@Getter
@Slf4j
@EqualsAndHashCode
@RequiredArgsConstructor
public class Tile {
    /**
     * The directions where this tile has walls.
     */
    private final Set<Direction> walls;

    /**
     * The position of the tile.
     */
    private final Position position;

    /**
     * The neighbours of this tile, in the order of {@link Direction#VALUES}.
     * When there is no neighbour (edge tile, or obscuring walls), a {@code null} value is contained.
     *
     * @apiNote The values have to be populated by {@link Level#calculateNeighbours()}
     */
    @Setter private transient List<Tile> neighbours;

    /**
     * Returns the neighbouring {@link Tile} in the specified {@link Direction}.
     * @param direction the direction
     * @return The found neighbour {@link Tile}, or {@code null}
     */
    @Nullable
    public Tile getNeighbour(Direction direction) {
        return neighbours.get(direction.ordinal());
    }

    @Override
    public String toString() {
        return position.toString();
    }

    /**
     * Adds or removes a wall in the specified direction.
     * This method handles wall deduplication. (Setting two opposing walls on neighbouring tiles won't be recorded twice)
     *
     * @param on the {@link Level} containing the tile
     * @param d the desired direction of the wall
     */
    public void setWall(Level on, Direction d) {
        boolean isWall = walls.contains(d);
        var otherPos = position.add(d.asOffset());
        if (!otherPos.isBoundedBy(Position.ZERO, Position.MAP_EDGE)) {
            if (isWall)
                walls.remove(d);
            else
                walls.add(d);
            return;
        }
        Tile other = on.get(otherPos);
        boolean isOtherWall = other.getWalls().contains(d.opposite());
        if (!isWall && !isOtherWall) {
            walls.add(d);
        }
        else if (isWall && !isOtherWall) {
            walls.remove(d);
        }
        else if (!isWall) {
            other.getWalls().remove(d.opposite());
        }
        else {
            walls.remove(d);
            other.getWalls().remove(d.opposite());
        }
    }

    /**
     * @return {@code true} when this {@link Tile} is at the edge of the grid.
     */
    public boolean isEdge() {
        return position.getX() == 0 || position.getX() == Constants.LEVEL_SIZE - 1 || position.getY() == 0 || position.getY() == Constants.LEVEL_SIZE - 1;
    }

    /**
     *  Calculates the directions where this {@link Tile} touches the edge of the grid.
     * @return The edge directions.
     */
    public Set<Direction> edgeSides() {
        Set<Direction> result = EnumSet.noneOf(Direction.class);
        for (var d : Direction.VALUES) {
            var o = position.add(d.asOffset());
            if (!o.isBoundedBy(Position.ZERO, Position.MAP_EDGE))
                result.add(d);
        }
        return result;
    }
}
