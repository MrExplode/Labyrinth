package me.sunstorm.labyrinth.core.level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Constants;
import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;

import java.io.IOException;
import java.nio.file.*;
import java.util.EnumSet;
import java.util.Set;

import static me.sunstorm.labyrinth.core.Constants.LEVEL_SIZE;

/**
 * A container of game {@link Level}s, managing the creation and persistence.
 */
@Slf4j
public class LevelStore {
    private final ObservableList<Level> levels = FXCollections.observableArrayList();
    private final Path levelDirectory;
    private final PathMatcher jsonMatcher;
    private final LevelGenerator generator = new LevelGenerator();
    private boolean loaded = false;

    /**
     * Creates a new {@link LevelStore} with the specified path as the base directory for level storage.
     *
     * @param levelDirectory the {@link Path} of the level directory.
     */
    public LevelStore(Path levelDirectory) {
        this.levelDirectory = levelDirectory;
        this.jsonMatcher = levelDirectory.getFileSystem().getPathMatcher("glob:**/*.json");
    }

    /**
     * Populates the store from disk for the first time.
     *
     * @throws IOException when an underlying IO exception occurs
     */
    public void load() throws IOException {
        if (loaded) return;
        reload();
        loaded = true;
    }

    /**
     * Discards the current {@link Level}, and loads the saved levels from disk.
     *
     * @throws IOException when an underlying IO exception occurs
     */
    public void reload() throws IOException {
        if (Files.notExists(levelDirectory)) Files.createDirectories(levelDirectory);
        levels.clear();
        extractDefault();
        try (var fs = Files.list(levelDirectory).filter(jsonMatcher::matches)) {
            fs.forEach(p -> {
                try {
                    log.debug("Loading: {}", p.getFileName());
                    var level = Constants.GSON.fromJson(Files.newBufferedReader(p), Level.class);
                    level.calculateNeighbours();
                    log.info("Loaded level: {}", level.getName());
                    levels.add(level);
                } catch (IOException e) {
                    log.error("Failed to load level (" + p.getFileName() + ")", e);
                }
            });
        }
        if (levels.isEmpty())
            levels.add(createEmpty());
    }

    private void extractDefault() throws IOException {
        Files.copy(getClass().getResourceAsStream("/levels/default.json"), levelDirectory.resolve("default.json"), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Saves the current list of levels to disk, and cleans up deleted level data.
     *
     * @throws IOException when an underlying IO exception occurs
     */
    public void save() throws IOException {
        levels.forEach(Level::calculateNeighbours);
        cleanupSaves();
        for (Level l : levels) {
            Files.writeString(levelDirectory.resolve(l.getName() + ".json"), Constants.GSON.toJson(l));
        }
    }


    private void cleanupSaves() throws IOException {
        try (var fs = Files.list(levelDirectory).filter(jsonMatcher::matches)) {
            var levelNames = levels.stream().map(l -> l.getName() + ".json").toList();
            var invalidPaths = fs.filter(p -> !levelNames.contains(p.getFileName().toString())).toList();
            for (Path p : invalidPaths) {
                log.info("Found invalidated level file ({}), deleting", p);
                Files.delete(p);
            }
        }
    }

    /**
     * Creates a new empty level. Neighbours are not populated in the {@link Tile}s.
     *
     * @return The new {@link Level}.
     */
    public Level createEmpty() {
        var level = new Level();
        for (int y = 0; y < LEVEL_SIZE; y++) {
            Set<Direction> vertical = switch (y) {
                case 0 -> EnumSet.of(Direction.UP);
                case LEVEL_SIZE - 1 -> EnumSet.of(Direction.DOWN);
                default -> EnumSet.noneOf(Direction.class);
            };
            for (int x = 0; x < LEVEL_SIZE; x++) {
                var position = Position.of(x, y);
                Set<Direction> walls = EnumSet.copyOf(vertical);
                walls.addAll(switch (x) {
                    case 0 -> EnumSet.of(Direction.LEFT);
                    case LEVEL_SIZE - 1 -> EnumSet.of(Direction.RIGHT);
                    default -> EnumSet.noneOf(Direction.class);
                });
                level.tiles[y][x] = new Tile(walls, position);
            }
        }
        return level;
    }

    /**
     * Generates a new randomized level using {@link LevelGenerator}.
     *
     * @return The randomized {@link Level}.
     */
    public Level createRandom() {
        var l = createEmpty();
        generator.randomize(l);
        return l;
    }

    /**
     * @return A list of all currently stored {@link Level}.
     */
    public ObservableList<Level> getLevels() {
        return levels;
    }
}
