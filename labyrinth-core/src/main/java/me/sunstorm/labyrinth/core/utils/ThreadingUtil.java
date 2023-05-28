package me.sunstorm.labyrinth.core.utils;

import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Constants;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Threading related utilities.
 */
@Slf4j
public class ThreadingUtil {

    /**
     * Runs a {@link Runnable} on the JavaFX {@link Thread}.
     *
     * @param task the task
     */
    public static void onFXThread(Runnable task) {
        try {
            Class<?> c = Class.forName("javafx.application.Platform");
            Method m = c.getMethod("runLater", Runnable.class);
            m.invoke(null, task);
        } catch (ReflectiveOperationException e) {
            log.error("Failed to delegate task to Platform#runLater", e);
        }
    }

    /**
     * Runs a {@link Runnable} on the JavaFX {@link Thread} after a given delay.
     *
     * @param task the task to be executed
     * @param amount the amount of delay
     * @param unit the {@link TimeUnit} of the given delay
     */
    public static void onFXThreadLater(Runnable task, int amount, TimeUnit unit) {
        Constants.SCHEDULER.schedule(() -> onFXThread(task), amount, unit);
    }
}
