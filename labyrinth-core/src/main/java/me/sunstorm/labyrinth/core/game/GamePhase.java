package me.sunstorm.labyrinth.core.game;

/**
 * Represents the phases of the {@link Game}.
 */
public enum GamePhase {
    /**
     * Player takes their turn.
     */
    PLAYER,

    /**
     * The monster takes their turn.
     */
    MONSTER,

    /**
     * The game has ended, either by the player escaping, or the monster catching the player.
     */
    END
}
