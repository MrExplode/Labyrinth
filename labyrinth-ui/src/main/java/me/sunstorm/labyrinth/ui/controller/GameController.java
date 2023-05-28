package me.sunstorm.labyrinth.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.game.Game;
import me.sunstorm.labyrinth.core.game.GamePhase;
import me.sunstorm.labyrinth.core.game.GameResultStore;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import me.sunstorm.labyrinth.ui.LabyrinthScene;
import me.sunstorm.labyrinth.ui.editor.NodeBuilder;

import javax.inject.Inject;
import java.util.stream.IntStream;

@Slf4j
public class GameController {
    private final GameResultStore resultStore;
    private final NodeBuilder nodeBuilder;
    private final Game game;
    @FXML
    public Button menuButton;
    @FXML
    public GridPane gridPane;

    @Inject
    public GameController(LabyrinthSettings settings, GameResultStore resultStore) {
        this.resultStore = resultStore;
        this.nodeBuilder = new NodeBuilder(LabyrinthScene.GAME, settings);
        this.game = new Game(settings.getSelectedLevel(), resultStore);
        // hehe funny global state transfer :DD
        game.getResult().setName(settings.getCurrentName());
        nodeBuilder.setGameClickConsumer(game::attemptMove);
        game.phaseProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == GamePhase.END) {
                LabyrinthScene.POST_GAME.transition(gridPane);
            }
        });
    }

    @FXML
    private void initialize() {
        IntStream.rangeClosed(0, 5).forEach(y -> IntStream.rangeClosed(0, 5).forEach(x ->
                gridPane.add(nodeBuilder.createLevelGridElement(game.getLevel(), x, y), x, y)
        ));
    }

    @FXML
    public void menuClicked(MouseEvent mouseEvent) {
        LabyrinthScene.SELECTOR.transition(mouseEvent.getSource());
    }
}
