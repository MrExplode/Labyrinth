package me.sunstorm.labyrinth.core.level;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;
import me.sunstorm.labyrinth.core.entity.Monster;
import me.sunstorm.labyrinth.core.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static me.sunstorm.labyrinth.core.Constants.LEVEL_SIZE;

/**
 * The level manages the grid of {@link Tile}s for the game.
 */
@Slf4j
public class Level implements Iterable<Tile> {
    protected final Tile[][] tiles;
    @Getter private final Player player;
    @Getter private final Monster monster;
    @Getter private final transient ObservableList<Position> playerMoveCandidates = FXCollections.observableArrayList();
    private final SimpleObjectProperty<String> name;

    protected Level() {
        tiles = new Tile[LEVEL_SIZE][LEVEL_SIZE];
        player = new Player(Position.ZERO);
        monster = new Monster(Position.of(LEVEL_SIZE - 1, LEVEL_SIZE - 1));
        name = new SimpleObjectProperty<>("unnamed");
    }

    private Level(Tile[][] tiles, Player player, Monster monster, String name) {
        this.tiles = tiles;
        this.player = player;
        this.monster = monster;
        this.name = new SimpleObjectProperty<>(name);
    }

    /**
     * Retrieves the tile at the given position.
     *
     * @param pos the {@link Position}
     * @return The {@link Tile}.
     */
    public Tile get(Position pos) {
        return tiles[pos.getY()][pos.getX()];
    }

    /**
     * Retrieves the tile at the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return The {@link Tile}.
     */
    public Tile get(int x, int y) {
        return tiles[y][x];
    }

    /**
     * Calculates each Tile's neighbours. (builds the pathfinding graph)
     */
    public void calculateNeighbours() {
        for (int y = 0; y < LEVEL_SIZE; y++) {
            for (int x = 0; x < LEVEL_SIZE; x++) {
                var pos = Position.of(x, y);
                List<Tile> availableNeighbours = new ArrayList<>();
                var self = tiles[y][x];
                for (var d : Direction.VALUES) {
                    var otherPos = pos.add(d.asOffset());
                    if (self.getWalls().contains(d) || !otherPos.isBoundedBy(Position.ZERO, Position.MAP_EDGE)) {
                        availableNeighbours.add(null);
                        continue;
                    }
                    var other = get(otherPos);
                    availableNeighbours.add(other.getWalls().contains(d.opposite()) ? null : other);
                }
                self.setNeighbours(availableNeighbours);
            }
        }
    }

    /**
     * Determines if the tile at the given position is the exit of the level.
     *
     * @param p the {@link Position} of the tile to be tested
     * @return {@code true} if the tile is the exit
     */
    public boolean isExitTile(Position p) {
        for (var d : Direction.VALUES) {
            var n = p.add(d.asOffset());
            if (!n.isBoundedBy(Position.ZERO, Position.MAP_EDGE) && !get(p).getWalls().contains(d))
                return true;
        }
        return false;
    }

    /**
     *  Creates a deep copy of this instance.
     *
     * @return the new instance
     */
    public Level copy() {
        Tile[][] newTiles = new Tile[tiles.length][tiles[0].length];
        for (int i = 0; i < newTiles.length; i++) {
            newTiles[i] = Arrays.copyOf(tiles[i], tiles[i].length);
        }
        var newPlayer = new Player(player.getPosition());
        var newMonster = new Monster(monster.getPosition());
        var newName = new String(name.get());
        return new Level(newTiles, newPlayer, newMonster, newName);
    }

    /**
     * Creates an iterator that can traverse all tiles of the level.
     *
     * @return A {@link Tile} iterator.
     */
    @NotNull
    @Override
    public Iterator<Tile> iterator() {
        return new Iterator<>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < LEVEL_SIZE * LEVEL_SIZE;
            }

            @Override
            public Tile next() {
                var current = tiles[index / LEVEL_SIZE][index % LEVEL_SIZE];
                index++;
                return current;
            }
        };
    }

    /**
     * Retrieves the name of this level.
     *
     * @return The level name.
     */
    public String getName() {
        return name.get();
    }

    /**
     * Retrieves the name property of this level.
     *
     * @return The name property.
     */
    public ObjectProperty<String> nameProperty() {
        return name;
    }
}
