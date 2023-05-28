package me.sunstorm.labyrinth.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import me.sunstorm.labyrinth.ui.LabyrinthScene;
import me.sunstorm.labyrinth.ui.utils.LocaleConverter;

import javax.inject.Inject;
import java.util.Locale;

@Slf4j
public class SettingsController {
    private final LabyrinthSettings settings;

    @FXML
    public Button menuButton;
    @FXML
    public ChoiceBox<Locale> languageChoice;

    @Inject
    public SettingsController(LabyrinthSettings settings) {
        this.settings = settings;
    }

    @FXML
    public void initialize() {
        languageChoice.setConverter(new LocaleConverter());
        languageChoice.getItems().add(Locale.ENGLISH);
        languageChoice.getItems().add(Locale.of("hu"));
        languageChoice.getSelectionModel().select(settings.getSelectedLocale());
        languageChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                settings.setSelectedLocale(newValue);
        });
    }

    public void menuClicked(MouseEvent mouseEvent) {
        LabyrinthScene.MENU.transition(mouseEvent.getSource());
    }
}
