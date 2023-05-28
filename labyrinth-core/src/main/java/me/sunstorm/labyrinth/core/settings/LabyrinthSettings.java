package me.sunstorm.labyrinth.core.settings;

import lombok.Getter;
import lombok.Setter;
import me.sunstorm.labyrinth.core.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The global settings for the application.
 */
@Getter
@Setter
public class LabyrinthSettings {
    private Locale selectedLocale = Locale.of("HU");
    /* as you might have noticed, I sneakily used the settings for temporary data transfer
    between otherwise unreachable instances */
    @Nullable
    private transient Level selectedLevel;
    @Nullable
    private transient String currentName;
}
