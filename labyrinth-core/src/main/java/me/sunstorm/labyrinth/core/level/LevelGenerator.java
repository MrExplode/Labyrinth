package me.sunstorm.labyrinth.core.level;

import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;
import me.sunstorm.labyrinth.core.routing.BreadthFirstPathfinder;
import me.sunstorm.labyrinth.core.routing.Pathfinder;

import java.util.*;

/**
 * This class provides level randomization features.
 */
@Slf4j
public class LevelGenerator {

    /**
     * Randomizes the specified level.
     *
     * @param level the {@link Level}
     */
    public void randomize(Level level) {
        long start = System.nanoTime();
        var random = new Random();
        level.nameProperty().set("generated_" + random.nextInt(100));
        Pathfinder<Position> pathfinder = new BreadthFirstPathfinder(level);
        int routeLength;
        int escapeLength;
        Position escapePos;
        do {
            level.iterator().forEachRemaining(t -> randomize(random, level, t));
            level.getPlayer().setPosition(Position.of(random.nextInt(0, 6), random.nextInt(0, 6)));
            level.getMonster().setPosition(Position.of(random.nextInt(0, 6), random.nextInt(0, 6)));

            var edges = collectEdges(level);
            edges.forEach(t -> t.edgeSides().forEach(d -> t.getWalls().add(d)));
            var escapeTile = edges.get(random.nextInt(edges.size() - 1));
            escapePos = escapeTile.getPosition();
            escapeTile.getWalls().remove(escapeTile.edgeSides().iterator().next());
            level.calculateNeighbours();

            var monsterPath = pathfinder.route(level.getMonster().getPosition(), level.getPlayer().getPosition());
            var escapePath = pathfinder.route(level.getPlayer().getPosition(), escapeTile.getPosition());
            routeLength = monsterPath == null ? -1 : monsterPath.size();
            escapeLength = escapePath == null ? -1 : escapePath.size();
        } while (routeLength < 12 ||
                escapeLength <= 5 ||
                level.getMonster().getPosition().distanceSquared(escapePos) <= level.getPlayer().getPosition().distanceSquared(escapePos)
        );
        log.info("Level generation took {} ms", (System.nanoTime() - start) / 1_000_000);
    }

    private List<Tile> collectEdges(Level l) {
        List<Tile> result = new ArrayList<>();
        l.iterator().forEachRemaining(t -> {
            if (t.isEdge()) result.add(t);
        });
        return result;
    }

    private void randomize(Random random, Level level, Tile t) {
        Set<Direction> edgeSides = t.edgeSides();
        level.calculateNeighbours();
        int wallCount = random.nextInt(0, 3);
        List<Direction> candidates = new ArrayList<>(List.of(Direction.VALUES));
        Collections.shuffle(candidates, random);
        for (int i = 0; i < wallCount; i++) {
            var candidate = candidates.get(i);
            if (edgeSides.contains(candidate)) continue;
            t.setWall(level, candidate);
        }
    }
}
