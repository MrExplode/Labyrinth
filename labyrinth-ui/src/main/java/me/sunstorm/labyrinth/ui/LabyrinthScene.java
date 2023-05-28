package me.sunstorm.labyrinth.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.inject.DependencyKey;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import me.sunstorm.labyrinth.ui.utils.AlertUtil;
import me.sunstorm.labyrinth.ui.utils.CssUtil;

import java.io.IOException;
import java.util.ResourceBundle;

@Slf4j
@RequiredArgsConstructor
public enum LabyrinthScene {
    SELECTOR("levelSelectorView"),
    GAME("gameView"),
    POST_GAME("postGameView"),
    EDITOR("editorView"),
    SETTINGS("settingsView"),
    MENU("menuView");

    private final String fxmlHandle;

    public void transition(Object on) {
        transition((Stage) ((Node) on).getScene().getWindow());
    }

    public void transition(Node on) {
        transition((Stage) on.getScene().getWindow());
    }

    public void transition(Stage on) {
        try {
            var loader = new FXMLLoader();
            var settings = (LabyrinthSettings) LabyrinthApplication.getInjector().getProvidedInstance(new DependencyKey(LabyrinthSettings.class, null));
            loader.setResources(ResourceBundle.getBundle("i18n/lang", settings.getSelectedLocale()));
            loader.setControllerFactory(LabyrinthApplication.getInjector()::createInstance);
            Parent root = loader.load(getClass().getResourceAsStream("/views/" + fxmlHandle + ".fxml"));
            Scene scene = new Scene(root);
            CssUtil.addGlobals(scene.getStylesheets());
            on.setScene(scene);
            on.show();
        } catch (IOException e) {
            log.error("Transition to " + this + " failed", e);
            AlertUtil.showExceptionError("Scene transition failed!", e);
        }
    }
}
