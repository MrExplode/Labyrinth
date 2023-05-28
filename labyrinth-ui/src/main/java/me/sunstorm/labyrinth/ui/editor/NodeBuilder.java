package me.sunstorm.labyrinth.ui.editor;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Constants;
import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import me.sunstorm.labyrinth.ui.LabyrinthScene;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
@Setter
public class NodeBuilder {
    private final LabyrinthScene target;
    private final LabyrinthSettings settings;
    private final double triangleWidth;
    private final Double[][] trianglePoints;
    @Getter
    @Nullable private ObjectProperty<Brush> activeBrushProperty;
    @Nullable private ObjectProperty<Level> currentLevelProperty;
    @Nullable private Runnable invalidationListener;
    @Nullable private Consumer<Position> gameClickConsumer;

    public NodeBuilder(LabyrinthScene target, LabyrinthSettings settings) {
        this.target = target;
        this.settings = settings;
        triangleWidth = 400 / 6d;
        trianglePoints = new Double[][] {
                {
                        0d, 0d,
                        0d, triangleWidth,
                        triangleWidth / 2, triangleWidth / 2
                },
                {
                        triangleWidth, 0d,
                        triangleWidth / 2, triangleWidth / 2,
                        triangleWidth, triangleWidth
                },
                {
                        0d, 0d,
                        triangleWidth / 2, triangleWidth / 2,
                        triangleWidth, 0d
                },
                {
                        triangleWidth / 2, triangleWidth / 2,
                        0d, triangleWidth,
                        triangleWidth, triangleWidth
                }
        };
        if (target == LabyrinthScene.EDITOR)
            activeBrushProperty = new SimpleObjectProperty<>(Brush.WALLS);
    }

    public Node createLevelListElement(Level level) {
        var pane = new HBox();
        pane.setPrefWidth(250);
        pane.getStyleClass().add("padding-20");
        var grid = createSmallGrid(level);
        if (currentLevelProperty != null) {
            currentLevelProperty.addListener(__ -> {
                pane.getChildren().remove(0);
                pane.getChildren().add(0, createSmallGrid(level));
            });
        }
        pane.getChildren().add(grid);

        var labelContainer = new StackPane();
        labelContainer.setAlignment(Pos.CENTER);
        labelContainer.setPrefWidth(120);

        var nameLabel = new Label();
        nameLabel.textProperty().bind(level.nameProperty());
        labelContainer.getChildren().add(nameLabel);
        pane.getChildren().add(labelContainer);
        pane.getStyleClass().add("modal-background-small");
        pane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");

        if (target == LabyrinthScene.EDITOR) {
            validate();
            pane.setOnMouseClicked(e -> currentLevelProperty.set(level));
            pane.styleProperty().bind(Bindings.when(currentLevelProperty.isEqualTo(level))
                    .then("-fx-background-color: rgba(255, 255, 255, 0.85);")
                    .otherwise("-fx-background-color: rgba(255, 255, 255, 0.5);"));
        } else if (target == LabyrinthScene.SELECTOR) {
            pane.setOnMouseEntered(e -> pane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);"));
            pane.setOnMouseExited(e -> pane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);"));
            pane.setOnMouseClicked(e -> {
                settings.setSelectedLevel(level.copy());
                LabyrinthScene.GAME.transition(e.getSource());
            });
        }
        return pane;
    }

    private Node createSmallGrid(Level level) {
        var grid = new GridPane();
        grid.getStyleClass().add("padding-15");
        var colConst = new ColumnConstraints();
        colConst.setHgrow(Priority.SOMETIMES);
        colConst.setMaxWidth(20);
        var rowConst = new RowConstraints();
        rowConst.setVgrow(Priority.SOMETIMES);
        rowConst.setMaxHeight(20);
        IntStream.rangeClosed(0, 5).forEach(i -> {
            grid.getColumnConstraints().add(colConst);
            grid.getRowConstraints().add(rowConst);
        });
        IntStream.rangeClosed(0, 5).forEach(y -> IntStream.rangeClosed(0, 5).forEach(x ->
                grid.add(createLevelGridElement(level, x, y, true), x, y)
        ));
        return grid;
    }

    public Node createLevelGridElement(Level level, int x, int y) {
        return createLevelGridElement(level, x, y, false);
    }

    private Node createLevelGridElement(Level level, int x, int y, boolean small) {
        var tile = level.get(x, y);
        var walls = new HashSet<>(tile.getWalls());
        var pane = new StackPane();
        pane.getChildren().add(createEntities(level, x, y, small));
        if (target == LabyrinthScene.EDITOR) {
            pane.setOnMouseClicked(e -> {
                if (activeBrushProperty.get() == Brush.PLAYER)
                    level.getPlayer().setPosition(Position.of(x, y));
                else if (activeBrushProperty.get() == Brush.MONSTER)
                    level.getMonster().setPosition(Position.of(x, y));
            });
            if (!small) {
                pane.getChildren().add(crateTriangles(level, tile));
            }
        } else if (target == LabyrinthScene.GAME) {
            Objects.requireNonNull(gameClickConsumer, "game click handler is not set up");
            // game move click
            pane.setOnMouseClicked(e -> gameClickConsumer.accept(Position.of(x, y)));
            pane.setMinWidth(500 / 6d);
            pane.setMinHeight(500 / 6d);
        }
        if (!small) {
            if (x - 1 >= 0 && level.get(x - 1, y).getWalls().contains(Direction.RIGHT))
                walls.add(Direction.LEFT);
            if (y - 1 >= 0 && level.get(x, y -1).getWalls().contains(Direction.DOWN))
                walls.add(Direction.UP);
        }
        pane.setStyle(createBorderStyle(walls, small));
        pane.getStyleClass().add("tile-base");
        return pane;
    }

    private Node createEntities(Level level, int x, int y, boolean small) {
        var circle = new Circle(small ? 5 : 23);
        if (!small) {
            circle.setTranslateX(-3);
            circle.setTranslateY(-3);
        }
        circle.fillProperty().bind(createEntityFillBinding(level, Position.of(x, y)));
        if (target == LabyrinthScene.EDITOR) {
            validate();
            var pos = Position.of(x, y);
            var timeline = new Timeline();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.setAutoReverse(true);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000),
                    new KeyValue(circle.scaleXProperty(), 0.8),
                    new KeyValue(circle.scaleYProperty(), 0.8)
            ));
            var animCondition = Bindings
                    .when(
                            activeBrushProperty
                                    .isEqualTo(Brush.PLAYER)
                                    .and(level.getPlayer().positionProperty().isEqualTo(pos)))
                    .then(true)
                    .otherwise(Bindings
                            .when(activeBrushProperty
                                    .isEqualTo(Brush.MONSTER)
                                    .and(level.getMonster().positionProperty().isEqualTo(pos)))
                            .then(true)
                            .otherwise(false));
            animCondition.addListener((observable, oldValue, newValue) -> {
                if (!oldValue && newValue && timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                } else {
                    timeline.jumpTo(Duration.millis(0));
                    timeline.stop();
                }
            });
            circle.setUserData(animCondition);
        } else if (target == LabyrinthScene.GAME) {
            level.getMonster().positionProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(Position.of(x, y))) {
                    var timeline = new Timeline();
                    var offset = oldValue.subtract(newValue);
                    circle.setTranslateX(offset.getX() * 50);
                    circle.setTranslateY(offset.getY() * 50);
                    double oldOrder = circle.getParent().getViewOrder();
                    circle.getParent().setViewOrder(-999);
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(Constants.MONSTER_ANIMATION_DURATION / 2f),
                            new KeyValue(circle.translateXProperty(), 0),
                            new KeyValue(circle.translateYProperty(), 0)
                    ));
                    timeline.setOnFinished(__ -> circle.getParent().setViewOrder(oldOrder));
                    timeline.playFromStart();
                }
            });
        }
        return circle;
    }

    private Node crateTriangles(Level level, Tile tile) {
        validate();
        var pane = new AnchorPane();
        for (var d : Direction.VALUES) {
            var polygon = new Polygon();
            polygon.getPoints().addAll(trianglePoints[d.ordinal()]);
            polygon.setFill(Color.TRANSPARENT);
            polygon.setOnMouseEntered(e -> {
                if (activeBrushProperty.get() == Brush.WALLS)
                    polygon.setFill(Color.rgb(125, 196, 121, 0.8));
            });
            polygon.setOnMouseExited(e -> {
                if (activeBrushProperty.get() == Brush.WALLS)
                    polygon.setFill(Color.TRANSPARENT);
            });
            polygon.setOnMouseClicked(e -> {
                if (activeBrushProperty.get() != Brush.WALLS) return;
                tile.setWall(level, d);
                invalidationListener.run();
            });
            pane.getChildren().add(polygon);
        }
        return pane;
    }

    @Nullable
    private ObservableValue<? extends Paint> createEntityFillBinding(Level level, Position pos) {
        return switch (target) {
            case EDITOR, SELECTOR -> Bindings
                    .when(level.getPlayer().positionProperty().isEqualTo(pos))
                    .then(Color.CORNFLOWERBLUE)
                    .otherwise(Bindings
                            .when(level.getMonster().positionProperty().isEqualTo(pos))
                            .then(Color.BLACK)
                            .otherwise(Color.TRANSPARENT)
                    );
            case GAME -> Bindings
                    .when(level.getPlayer().positionProperty().isEqualTo(pos))
                    .then(Color.CORNFLOWERBLUE)
                    .otherwise(Bindings
                            .when(level.getMonster().positionProperty().isEqualTo(pos))
                            .then(Color.BLACK)
                            .otherwise(Bindings
                                    .when(Bindings.createBooleanBinding(() -> level.getPlayerMoveCandidates().contains(pos), level.getPlayerMoveCandidates()))
                                    .then(Color.rgb(51, 181, 199, 0.5))
                                    .otherwise(Color.TRANSPARENT)
                            )
                    );
            default -> null;
        };
    }

    private String createBorderStyle(HashSet<Direction> walls, boolean small) {
        var widthBuilder = new StringBuilder("-fx-border-width:");
        var paddingBuilder = new StringBuilder("-fx-padding:");

        Direction.CSS_ORDER.forEach(d -> {
            if (walls.contains(d)) {
                if (small)
                    widthBuilder.append(" 2px");
                else
                    widthBuilder.append(" 4px");
                paddingBuilder.append(" 0px");
            } else {
                if (small) {
                    paddingBuilder.append(" 1.5px");
                    widthBuilder.append(" 0.5px");
                } else {
                    paddingBuilder.append(" 3px");
                    widthBuilder.append(" 1px");
                }
            }
        });
        return widthBuilder.append("; ").toString() + paddingBuilder.append(";");
    }

    private void validate() {
        if (target == LabyrinthScene.EDITOR) {
            String message = "Missing builder field (%s) for target scene: %s";
            Objects.requireNonNull(activeBrushProperty, String.format(message, "activeBrush", target));
            Objects.requireNonNull(currentLevelProperty, String.format(message, "currentLevel", target));
            Objects.requireNonNull(invalidationListener, String.format(message, "invalidationListener", target));
        }
    }
}
