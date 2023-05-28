package me.sunstorm.labyrinth.core.entity;

import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.routing.BreadthFirstPathfinder;
import me.sunstorm.labyrinth.core.routing.Pathfinder;

import java.util.List;

/**
 * Represents a non-player character. The player must run from this entity.
 */
@Slf4j
public class Monster extends Entity {
    private final transient int STEP_RANGE = 2;

    /**
     * Creates a new monster.
     *
     * @param position the starting position
     */
    public Monster(Position position) {
        super(position);
    }

    /**
     * Calculates the next move for the Monster.
     *
     * @param on the {@link Level} to move on
     * @param to the target {@link Position}
     * @return The list of {@link Position}s to take for the next step, in order.
     */
    public List<Position> nextMove(Level on, Position to) {
        log.debug("Calculating route from ({}) to ({})", getPosition(), to);
        long start = System.nanoTime();
        Pathfinder<Position> finder = new BreadthFirstPathfinder(on);
        var route = finder.route(getPosition(), to, -1);
        log.debug("pathfinding took {} µs", (System.nanoTime() - start) / 1000);
        return route.subList(1, Math.min(route.size(), STEP_RANGE + 1));
    }
}
