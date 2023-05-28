package me.sunstorm.labyrinth.core.inject;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a key used during the registration of providers for dependency injection.
 * Adds support for supplying multiple dependencies of the same type by introducing name qualifiers.
 *
 * @param type The type of the provided dependency.
 * @param name The name qualifier of the provided dependency, {@code null} by default.
 */
public record DependencyKey(Class<?> type, @Nullable String name) {}
