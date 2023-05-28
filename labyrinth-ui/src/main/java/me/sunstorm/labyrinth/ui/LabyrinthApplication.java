package me.sunstorm.labyrinth.ui;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.sunstorm.labyrinth.core.Constants;
import me.sunstorm.labyrinth.core.inject.Injector;

import java.util.List;
import java.util.stream.Stream;

public class LabyrinthApplication extends Application {
    private static Injector injector;

    public static Injector getInjector() {
        if (injector == null) {
            injector = new Injector();
            injector.addProviderFactory(new DependencyFactory(injector));
        }
        return injector;
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Labyrinth");
        stage.setResizable(false);
        stage.getIcons().addAll(getAppIcons());
        stage.setOnCloseRequest(__ -> Constants.SCHEDULER.shutdown());
        LabyrinthScene.MENU.transition(stage);
    }

    private List<Image> getAppIcons() {
        return Stream.of("16", "32", "64", "128", "256").map(i -> new Image(getClass().getResourceAsStream("/icons/icon_" + i + ".png"))).toList();
    }
}
