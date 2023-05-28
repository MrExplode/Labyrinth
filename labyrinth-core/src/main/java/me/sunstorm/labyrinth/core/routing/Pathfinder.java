package me.sunstorm.labyrinth.core.routing;

import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A pathfinder that can find a route between specified nodes in a graph.
 *
 * @param <T> the type of the node
 */
@Slf4j
public abstract class Pathfinder<T> {
    /**
     * A constant indicating the pathfinder implementation may not limit the number of tries.
     */
    public static final int NO_MAX_TRIES = -1;

    /**
     * Calculates a route fom the start node to the target node.
     *
     * @param from the start node
     * @param to the target node
     * @return The list of nodes in the route.
     */
    public List<T> route(T from, T to) {
        return route(from, to, 1000);
    }

    /**
     * Calculates a route from the start node to the target node.
     *
     * @param from the start node
     * @param to the target node
     * @param maxTries the number of max tries, or {@link Pathfinder#NO_MAX_TRIES}
     * @return The list of nodes in the route, or {@code null} if max tries reached without finding a result.
     */
    @Nullable
    public abstract List<T> route(T from, T to, int maxTries);

    /**
     * Traces back a path of traceable nodes to the origin.
     *
     * @param traceable The end traceable node
     * @param <U> the type of the traceable node
     * @return A list of the nodes in the graph in the original traversing order.
     */
    public <U> List<U> backtrace(Traceable<U> traceable) {
        log.debug("backtracing...");
        List<U> route = new ArrayList<>();
        route.add(traceable.getNode());
        while (traceable.getParent() != null) {
            traceable = traceable.getParent();
            route.add(traceable.getNode());
        }
        log.debug("path backward: {}", route);
        Collections.reverse(route);
        log.debug("path forward: {}", route);
        return route;
    }
}
