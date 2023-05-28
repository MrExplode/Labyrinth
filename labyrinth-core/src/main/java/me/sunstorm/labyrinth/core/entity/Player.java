package me.sunstorm.labyrinth.core.entity;

import me.sunstorm.labyrinth.core.Position;

/**
 * Represents a user controlled {@link Entity}.
 */
public class Player extends Entity {

    /**
     * Creates a new player.
     *
     * @param position the starting position
     */
    public Player(Position position) {
        super(position);
    }
}
