package me.sunstorm.labyrinth.core.inject;

import java.lang.annotation.*;

/**
 * Marks a method as a dependency provider.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Provides {
}
