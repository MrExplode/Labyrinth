package me.sunstorm.labyrinth.core.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import lombok.EqualsAndHashCode;
import me.sunstorm.labyrinth.core.Position;

/**
 * Represents an entity in the game (duh!).
 */
@EqualsAndHashCode
public abstract class Entity {
    private final ObjectProperty<Position> position;

    /**
     * Creates a new entity at the specified position.
     * @param position the starting position
     */
    public Entity(Position position) {
        this.position = new ReadOnlyObjectWrapper<>(position);
    }

    /**
     * Retrieves the position of the entity.
     *
     * @return The current position.
     */
    public Position getPosition() {
        return position.get();
    }

    /**
     * Sets the position of the entity.
     *
     * @param pos the position
     */
    public void setPosition(Position pos) {
        position.set(pos);
    }

    /**
     * Retrieves the position property of the entity.
     *
     * @return The position property.
     */
    public ObjectProperty<Position> positionProperty() {
        return position;
    }
}
