package me.sunstorm.labyrinth.core;

import com.google.gson.Gson;
import me.sunstorm.labyrinth.core.serialization.LocalDateTimeSerializer;
import me.sunstorm.labyrinth.core.serialization.LocaleSerializer;
import me.sunstorm.labyrinth.core.serialization.TileSerializer;
import org.hildan.fxgson.FxGson;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This interface holds the global constants used through the lifecycle of the application.
 */
public interface Constants {

    /**
     * Global {@link Gson} instance, configured for the specific use case.
     */
    Gson GSON = FxGson.coreBuilder().setPrettyPrinting()
            .registerTypeAdapter(Tile.class, new TileSerializer())
            .registerTypeAdapter(Locale.class, new LocaleSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .create();

    /**
     * The size of the labyrinth grid.
     */
    int LEVEL_SIZE = 6;

    /**
     * This is the root path of the application output, everything else is resolved from this path.
     */
    Path BASE_DIR = Path.of("");

    /**
     * The path of the level storage directory.
     */
    Path LEVEL_DIR = BASE_DIR.resolve("levels");

    /**
     * A global scheduler for async tasks.
     */
    ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * The animation time of the monster's step in milliseconds.
     */
    int MONSTER_ANIMATION_DURATION = 400;
}
