package me.sunstorm.labyrinth.core.utils;

import me.sunstorm.labyrinth.core.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Utility methods for Json loading / saving.
 */
public interface JsonUtils {
    /**
     * Internal logger.
     *
     * @apiNote should not be used outside of {@link JsonUtils}
     */
    Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * Loads an instance of the specified type with {@link Constants#GSON}.
     *
     * @param type the desired type
     * @param from the path of the json file to load
     * @param <T> the type
     * @return The instance created from json.
     * @throws IOException when an underlying exception happens
     */
    static <T> T load(Class<T> type, Path from) throws IOException {
        return Constants.GSON.fromJson(Files.newBufferedReader(from), type);
    }

    /**
     * Loads an instance of the specified type with {@link Constants#GSON}.
     * If an exception occurs, an empty {@link Optional} returned.
     *
     * @param type the desired type
     * @param from the path of the json file to load
     * @param <T> the type
     * @return An optional of the given type.
     */
    static <T> Optional<T> loadOptional(Class<T> type, Path from) {
        try {
            return Optional.of(load(type, from));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Serializes and saves the given object, and silently catches any occurring {@link IOException}.
     * @param o the object
     * @param to the path for the saved json file
     */
    static void saveSilent(Object o, Path to) {
        try {
            save(o, to);
        } catch (IOException e) {
            log.warn("Saving " + o.getClass().getSimpleName() + " to " + to + " failed", e);
        }
    }

    /**
     * Serializes and saves the given object using {@link Constants#GSON}.
     * @param o the object
     * @param to the path for the saved json file
     * @throws IOException when an IO error occurs
     */
    static void save(Object o, Path to) throws IOException {
        Files.writeString(to, Constants.GSON.toJson(o));
    }
}
