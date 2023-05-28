package me.sunstorm.labyrinth.core.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Constants;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class handles the storage of the {@link GameResult}s.
 */
@Slf4j
public class GameResultStore {
    /**
     * This comparator determines the ranking of the game results.
     */
    public static final Comparator<GameResult> RESULT_COMPARATOR = Comparator
            .comparing(GameResult::isEscaped).reversed()
            .thenComparing(GameResult::getSteps)
            .thenComparing(GameResult::getDuration);

    private final Path resultsFile;
    private final List<GameResult> results = new ArrayList<>();
    @Nullable
    @Getter private GameResult latestResult;

    /**
     * Creates a new GameResultStore, with the specified {@link Path} as the directory for the save data.
     *
     * @param dir the save data directory
     */
    public GameResultStore(Path dir) {
        this.resultsFile = dir.resolve("results.json");
    }

    /**
     * Loads the persisted game results from the disk.
     *
     * @throws IOException when an underlying IO error occurs
     */
    public void load() throws IOException {
        if (Files.notExists(resultsFile)) {
            log.warn("No results file found, load skipped");
            return;
        }
        JsonArray data = JsonParser.parseReader(Files.newBufferedReader(resultsFile)).getAsJsonArray();
        for (JsonElement res : data)
            results.add(Constants.GSON.fromJson(res, GameResult.class));
        log.info("Loaded {} game result(s)", results.size());
    }

    /**
     * Persists the currently loaded game results to the disk.
     *
     * @throws IOException when an underlying IO error occurs
     */
    public void save() throws IOException {
        if (results.size() == 0) {
            log.info("No results found, skipping save");
            return;
        }
        log.info("Saving {} game result(s)", results.size());
        JsonArray data = new JsonArray();
        results.forEach(r -> data.add(Constants.GSON.toJsonTree(r)));
        Files.writeString(resultsFile, data.toString());
    }

    /**
     * Retrieves the list of game results, in the order of their ranking.
     *
     * @return the {@link GameResult} list
     */
    public List<GameResult> getResults() {
        return new ArrayList<>(results);
    }

    /**
     * Adds a new game results to the store.
     *
     * @param result the new game result
     */
    public void addResult(GameResult result) {
        results.add(result);
        results.sort(RESULT_COMPARATOR);
        latestResult = result;
    }
}
