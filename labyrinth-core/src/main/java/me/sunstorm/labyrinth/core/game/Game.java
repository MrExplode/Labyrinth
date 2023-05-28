package me.sunstorm.labyrinth.core.game;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.labyrinth.core.Constants;
import me.sunstorm.labyrinth.core.Position;
import me.sunstorm.labyrinth.core.Tile;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.utils.ThreadingUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Represents a game on a specific {@link Level}.
 */
@Slf4j
public class Game {
    private final Function<Level, Boolean> winCondition = l -> l.isExitTile(l.getPlayer().getPosition());
    private final Function<Level, Boolean> loseCondition = l -> l.getMonster().getPosition().equals(l.getPlayer().getPosition());

    @Getter
    private final Level level;
    private final GameResultStore resultStore;
    @Getter
    private final GameResult result;
    private final ReadOnlyObjectWrapper<GamePhase> phase = new ReadOnlyObjectWrapper<>(GamePhase.PLAYER);

    /**
     * Creates a new Game on the specified level.
     *
     * @param level the level to be played
     * @param resultStore the result store
     */
    public Game(Level level, GameResultStore resultStore) {
        this.level = level;
        this.resultStore = resultStore;
        this.result = new GameResult();
        log.info("Starting game on level: {}", level.getName());
        result.setStart(LocalDateTime.now());
        level.getPlayerMoveCandidates().addAll(getLegalMoves());

        phase.addListener((__, oldPhase, newPhase) -> {
            log.debug("changing phase from {} to {}", oldPhase, newPhase);
            if (newPhase == GamePhase.END) return;

            var steps = level.getPlayerMoveCandidates();
            steps.clear();
            if (newPhase == GamePhase.PLAYER)
                steps.addAll(getLegalMoves());

            if (winCondition.apply(level))
                endGame(true);
            else if (loseCondition.apply(level))
                endGame(false);
            else if (newPhase == GamePhase.MONSTER)
                doMonsterMove();
        });
    }

    private void endGame(boolean win) {
        result.setEscaped(win);
        result.setEnd(LocalDateTime.now());
        result.setLevelName(level.getName());
        resultStore.addResult(result);
        phase.set(GamePhase.END);
    }

    /**
     * Attempt a move to the given position.
     * @param to the position input from the user
     */
    public void attemptMove(Position to) {
        var player = level.getPlayer();
        boolean validMove = getLegalMoves().contains(to) && !player.getPosition().equals(to);
        if (phase.get() == GamePhase.PLAYER && validMove) {
            player.setPosition(to);
            result.addStep();
            phase.set(GamePhase.MONSTER);
        }
    }

    private void doMonsterMove() {
        var monster = level.getMonster();
        var route = monster.nextMove(level, level.getPlayer().getPosition());
        ThreadingUtil.onFXThreadLater(() -> {
            if (phase.get() != GamePhase.END) phase.set(GamePhase.PLAYER);
        }, (route.size() + 1) * Constants.MONSTER_ANIMATION_DURATION, TimeUnit.MILLISECONDS);
        for (int i = 0; i < route.size(); i++) {
            int finalI = i;
            ThreadingUtil.onFXThreadLater(() -> monster.setPosition(route.get(finalI)), (i + 1) * Constants.MONSTER_ANIMATION_DURATION, TimeUnit.MILLISECONDS);
        }
    }


    private List<Position> getLegalMoves() {
        return level.get(level.getPlayer().getPosition()).getNeighbours().stream().filter(Objects::nonNull).map(Tile::getPosition).toList();
    }

    /**
     * Retrieves the current game phase.
     *
     * @return the game phase
     */
    public GamePhase getPhase() {
        return phase.get();
    }

    /**
     * Retrieves the game phase property.
     *
     * @return the phase property
     */
    public ReadOnlyObjectProperty<GamePhase> phaseProperty() {
        return phase.getReadOnlyProperty();
    }
}
