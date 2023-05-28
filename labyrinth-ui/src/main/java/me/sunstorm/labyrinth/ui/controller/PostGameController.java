package me.sunstorm.labyrinth.ui.controller;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.game.GameResult;
import me.sunstorm.labyrinth.core.game.GameResultStore;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import me.sunstorm.labyrinth.ui.LabyrinthScene;
import me.sunstorm.labyrinth.ui.utils.RestrictedTableViewSelectionModel;

import javax.inject.Inject;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Slf4j
public class PostGameController {
    private final GameResultStore resultStore;
    private final LabyrinthSettings settings;

    @FXML
    private TableView<GameResult> highscoreTable;
    @FXML
    public TableColumn<GameResult, Integer> rankColumn;
    @FXML
    public TableColumn<GameResult, String> nameColumn;
    @FXML
    public TableColumn<GameResult, String> escapedColumn;
    @FXML
    public TableColumn<GameResult, Integer> stepsColumn;
    @FXML
    public TableColumn<GameResult, String> startColumn;
    @FXML
    public TableColumn<GameResult, String> durationColumn;
    @FXML
    public TableColumn<GameResult, String> mapColumn;
    @FXML
    public Label titleLabel;
    @FXML
    public Label levelLabel;
    @FXML
    public Label stepsLabel;
    @FXML
    public Label durationLabel;


    @Inject
    public PostGameController(GameResultStore resultStore, LabyrinthSettings settings) {
        this.resultStore = resultStore;
        this.settings = settings;
    }

    @FXML
    private void initialize() {
        var res = resultStore.getLatestResult();
        log.info("got result: {}", res);
        var i18n = ResourceBundle.getBundle("i18n/lang", settings.getSelectedLocale());
        setupTable(res, i18n);
        titleLabel.setText(res.isEscaped() ? i18n.getString("result.win") : i18n.getString("result.loss"));
        levelLabel.setText(i18n.getString("result.map") + ": " + res.getLevelName());
        stepsLabel.setText(i18n.getString("result.steps") + ": " + res.getSteps());
        durationLabel.setText(i18n.getString("result.duration") + ": " + res.getDuration().toString().substring(2));
    }

    private void setupTable(GameResult latest, ResourceBundle i18n) {
        highscoreTable.setSelectionModel(new RestrictedTableViewSelectionModel<>(highscoreTable));
        rankColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    setText((getTableRow().getIndex() + 1) + "");
                } else {
                    setText("");
                }
            }
        });
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName() == null ? i18n.getString("result.unknown") : data.getValue().getName()));
        escapedColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().isEscaped() ? i18n.getString("result.yes") : i18n.getString("result.no")));
        stepsColumn.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getSteps()).asObject());
        var formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm:ss");
        startColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatter.format(data.getValue().getStart())));
        durationColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDuration().toString().substring(2)));
        mapColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLevelName()));
        ObservableList<GameResult> list = FXCollections.observableArrayList(resultStore.getResults());
        highscoreTable.setItems(list);
        highscoreTable.getSelectionModel().select(latest);
    }

    public void menuClicked(MouseEvent mouseEvent) {
        LabyrinthScene.MENU.transition(mouseEvent.getSource());
    }
}
