package me.sunstorm.labyrinth.ui.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Constants;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.level.LevelStore;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import me.sunstorm.labyrinth.core.utils.ThreadingUtil;
import me.sunstorm.labyrinth.ui.LabyrinthScene;
import me.sunstorm.labyrinth.ui.editor.Brush;
import me.sunstorm.labyrinth.ui.editor.NodeBuilder;
import me.sunstorm.labyrinth.ui.utils.AlertUtil;
import me.sunstorm.labyrinth.ui.utils.NestedObjectProperty;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

@Slf4j
public class EditorController {
    private final LevelStore levelStore;
    private final ToggleGroup brushGroup;
    private final NodeBuilder nodeBuilder;
    private final SimpleObjectProperty<Level> currentLevel = new SimpleObjectProperty<>();
    private final LabyrinthSettings settings;
    private boolean generationInProgress = false;

    @FXML
    public Button menuButton;
    @FXML
    public GridPane gridPane;
    @FXML
    public Button reloadButton;
    @FXML
    public TextField levelNameField;
    @FXML
    public RadioButton wallPicker;
    @FXML
    public RadioButton playerPicker;
    @FXML
    public RadioButton monsterPicker;
    @FXML
    public VBox levelsPane;
    @FXML
    public Button deleteButton;
    @FXML
    public ScrollPane scrollPane;

    @Inject
    public EditorController(LevelStore levelStore, LabyrinthSettings settings) {
        this.levelStore = levelStore;
        this.brushGroup = new ToggleGroup();
        this.nodeBuilder = new NodeBuilder(LabyrinthScene.EDITOR, settings);
        this.settings = settings;
        nodeBuilder.setInvalidationListener(this::fillGridPane);
        nodeBuilder.setCurrentLevelProperty(currentLevel);
    }

    @FXML
    private void initialize() {
        reloadButton.setPrefWidth(Control.USE_COMPUTED_SIZE);
        wallPicker.setToggleGroup(brushGroup);
        playerPicker.setToggleGroup(brushGroup);
        monsterPicker.setToggleGroup(brushGroup);
        currentLevel.set(levelStore.getLevels().get(0));

        fillGridPane();

        var binding = new NestedObjectProperty<>(currentLevel, Level::nameProperty);
        levelNameField.textProperty().bindBidirectional(binding);
        nodeBuilder.getActiveBrushProperty().bind(Bindings.when(brushGroup.selectedToggleProperty().isEqualTo(wallPicker))
                .then(Brush.WALLS)
                .otherwise(Bindings.when(brushGroup.selectedToggleProperty().isEqualTo(playerPicker))
                        .then(Brush.PLAYER)
                        .otherwise(Brush.MONSTER)));

        fillListPane();
        levelStore.getLevels().addListener((ListChangeListener<? super Level>) l -> fillListPane());
        currentLevel.addListener(__ -> fillGridPane());

        //scroll to top
        Platform.runLater(() -> scrollPane.setVvalue(0));
    }

    private void fillListPane() {
        levelsPane.getChildren().clear();
        levelStore.getLevels().forEach(l -> levelsPane.getChildren().add(nodeBuilder.createLevelListElement(l)));

        var listControl = new HBox();
        listControl.setAlignment(Pos.CENTER);
        listControl.getStyleClass().add("padding-y-20");

        var i18n = ResourceBundle.getBundle("i18n/lang", settings.getSelectedLocale());
        var createButton = new Button();
        createButton.setText(i18n.getString("editor.new"));
        createButton.setOnMouseClicked(__ -> {
            var levels = levelStore.getLevels();
            levels.add(levelStore.createEmpty());
            currentLevel.set(levels.get(levels.size() - 1));
            // scroll to bottom
            Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
        });
        createButton.getStyleClass().add("basic-button");
        var randomButton = new Button();
        randomButton.setText(i18n.getString("editor.random"));
        var progress = new ProgressIndicator();
        progress.setStyle("-fx-progress-color: green;");
        progress.setMaxHeight(30d);
        progress.setMaxWidth(30d);
        randomButton.setOnMouseClicked(__ -> {
            if (generationInProgress) return;
            generationInProgress = true;
            listControl.getChildren().remove(randomButton);
            listControl.getChildren().add(progress);
            Constants.SCHEDULER.execute(() -> {
                // async generation
                var level = levelStore.createRandom();
                ThreadingUtil.onFXThread(() -> {
                    // UI mutation done back on FX thread
                    var levels = levelStore.getLevels();
                    levels.add(level);
                    currentLevel.set(levels.get(levels.size() - 1));
                    Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
                    generationInProgress = false;
                    listControl.getChildren().remove(progress);
                    listControl.getChildren().add(randomButton);
                });
            });
        });
        randomButton.getStyleClass().add("basic-button");
        listControl.getChildren().add(createButton);
        var spacer = new Label();
        spacer.getStyleClass().add("padding-x-10");
        listControl.getChildren().add(spacer);
        listControl.getChildren().add(randomButton);
        levelsPane.getChildren().add(listControl);
    }

    private void fillGridPane() {
        gridPane.getChildren().clear();
        for (int y = 0; y < gridPane.getColumnCount(); y++) {
            for (int x = 0; x < gridPane.getRowCount(); x++) {
                gridPane.add(nodeBuilder.createLevelGridElement(currentLevel.get(), x, y), x, y);
            }
        }
    }

    @FXML
    public void menuClicked(MouseEvent mouseEvent) {
        try {
            levelStore.save();
        } catch (IOException e) {
            AlertUtil.showExceptionError("Failed to save levels", e);
        }
        LabyrinthScene.MENU.transition(mouseEvent.getSource());
    }

    @FXML
    public void reloadClicked(MouseEvent mouseEvent) {
        try {
            levelStore.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onDeleteClicked(MouseEvent mouseEvent) {
        var levels = levelStore.getLevels();
        var level = currentLevel.get();
        int index = levels.indexOf(level);
        levels.remove(currentLevel.get());
        if (levels.isEmpty())
            levels.add(levelStore.createEmpty());
        currentLevel.set(levels.get(Math.max(0, index - 1)));
    }
}
