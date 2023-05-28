package me.sunstorm.labyrinth.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import me.sunstorm.labyrinth.core.level.LevelStore;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import me.sunstorm.labyrinth.ui.LabyrinthScene;
import me.sunstorm.labyrinth.ui.editor.NodeBuilder;

import javax.inject.Inject;

public class SelectorController {
    private final LevelStore store;
    private final NodeBuilder nodeBuilder;
    private final LabyrinthSettings settings;
    @FXML
    public FlowPane flowPane;
    @FXML
    public Button menuButton;
    @FXML
    public TextField nameField;

    @Inject
    public SelectorController(LevelStore store, LabyrinthSettings settings) {
        this.store = store;
        this.settings = settings;
        this.nodeBuilder = new NodeBuilder(LabyrinthScene.SELECTOR, settings);
    }

    @FXML
    private void initialize() {
        store.getLevels().forEach(l -> flowPane.getChildren().add(nodeBuilder.createLevelListElement(l)));
        nameField.focusedProperty().addListener((___, __, focused) -> {
            if (!focused)
                settings.setCurrentName(nameField.getText());
        });
    }

    @FXML
    public void menuClicked(MouseEvent mouseEvent) {
        LabyrinthScene.MENU.transition(mouseEvent.getSource());
    }
}
