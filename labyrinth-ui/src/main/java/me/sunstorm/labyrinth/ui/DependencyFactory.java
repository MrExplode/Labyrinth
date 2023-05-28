package me.sunstorm.labyrinth.ui;

import me.sunstorm.labyrinth.core.Constants;
import me.sunstorm.labyrinth.core.game.GameResultStore;
import me.sunstorm.labyrinth.core.inject.Injector;
import me.sunstorm.labyrinth.core.inject.Provides;
import me.sunstorm.labyrinth.core.level.LevelStore;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import me.sunstorm.labyrinth.core.utils.JsonUtils;

import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Path;

public class DependencyFactory {

    public DependencyFactory(Injector injector) {
        injector.addSimpleProvider(Path.class, "base-dir", () -> Constants.BASE_DIR);
        injector.addSimpleProvider(Path.class, "level-dir", () -> Constants.LEVEL_DIR);
    }

    @Provides
    LevelStore createLevelStore(@Named("level-dir") Path dir) throws IOException {
        var store = new LevelStore(dir);
        store.load();
        return store;
    }

    @Provides
    LabyrinthSettings createSettings(@Named("base-dir") Path dir) {
        var settingsPath = dir.resolve("settings.json");
        var settings = JsonUtils.loadOptional(LabyrinthSettings.class, settingsPath).orElseGet(LabyrinthSettings::new);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> JsonUtils.saveSilent(settings, settingsPath)));
        return settings;
    }

    @Provides
    GameResultStore createResultStore(@Named("base-dir") Path dir) throws IOException {
        var store = new GameResultStore(dir);
        store.load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                store.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        return store;
    }
}
