package me.sunstorm.labyrinth.core.routing;

import lombok.*;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a traceable node used in backtracking graphs during pathfinding.
 * Each traceable node holds a reference to its associated node and optionally a reference to its parent node in the graph.
 * @param <T> The type of the associated node.
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Traceable<T> {
    /**
     * The associated node.
     */
    private final T node;

    /**
     * The parent node, or null if unspecified.
     */
    @Nullable
    private Traceable<T> parent;
}
