package me.sunstorm.labyrinth.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.ui.LabyrinthScene;

@Slf4j
public class MenuController {
    @FXML
    public Button playButton;
    @FXML
    public Button editorButton;
    @FXML
    public Button settingsButton;

    @FXML
    public void playClick(MouseEvent mouseEvent) {
        LabyrinthScene.SELECTOR.transition(mouseEvent.getSource());
    }

    public void editorClick(MouseEvent mouseEvent) {
        LabyrinthScene.EDITOR.transition(mouseEvent.getSource());
    }

    public void settingsClick(MouseEvent mouseEvent) {
        LabyrinthScene.SETTINGS.transition(mouseEvent.getSource());
    }
}
