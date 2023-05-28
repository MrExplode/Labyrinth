package me.sunstorm.labyrinth.core.routing;

import lombok.RequiredArgsConstructor;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;
import me.sunstorm.labyrinth.core.level.Level;

import java.util.*;

/**
 * A {@link Pathfinder} implementation using breadth-first algorithm.
 */
@RequiredArgsConstructor
public class BreadthFirstPathfinder extends Pathfinder<Position> {
    private final Level level;

    @Override
    public List<Position> route(Position from, Position to, int maxTries) {
        Queue<Traceable<Tile>> queue = new LinkedList<>();
        Traceable<Tile> tile = new Traceable<>(level.get(from));
        queue.add(tile);
        int count = 0;
        while (!queue.isEmpty() && (maxTries == NO_MAX_TRIES || ++count <= maxTries)) {
            tile = queue.poll();
            if (tile.getNode().getPosition().equals(to))
                return backtrace(tile).stream().map(Tile::getPosition).toList();

            for (var n : tile.getNode().getNeighbours()) {
                if (n != null)
                    queue.offer(new Traceable<>(n, tile));
            }
        }
        return null;
    }
}
