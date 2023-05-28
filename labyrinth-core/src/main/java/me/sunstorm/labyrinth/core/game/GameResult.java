package me.sunstorm.labyrinth.core.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.sunstorm.labyrinth.core.level.LevelStore;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents the result of a {@link Game}.
 */
@Getter
@Setter
@ToString
public class GameResult {
    /**
     * Whether the player made it out of the labyrinth.
     */
    private boolean escaped = false;

    /**
     * The name of the player.
     */
    private String name;

    /**
     * The name of the level where the game took place. The named level may not exist in the {@link LevelStore}.
     */
    private String levelName;

    /**
     * The count of steps made by the player during the {@link Game}.
     */
    @Setter(AccessLevel.NONE)
    private int steps = 0;

    /**
     * The start time of the {@link Game}.
     */
    private LocalDateTime start;

    /**
     * The end time of the {@link Game}.
     */
    private LocalDateTime end;

    /**
     * Increments the recorded steps by one.
     */
    public void addStep() {
        steps++;
    }

    /**
     * Calculates the duration of the game.
     *
     * @return The {@link Duration} between the start and end.
     */
    public Duration getDuration() {
        return Duration.between(start, end);
    }
}
