package me.sunstorm.labyrinth.core.tile;

import me.sunstorm.labyrinth.core.Direction;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.level.LevelStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

public class TileTests {
    private Level level;

    @BeforeEach
    void setup() {
        level = new LevelStore(Path.of("")).createEmpty();
    }

    @Test
    void testToString() {
        assertThat(level.get(0, 0).toString()).isEqualTo("0 0");
    }

    @Test
    void testGetNeighbour() {
        level.calculateNeighbours();
        assertThat(level.get(0, 0).getNeighbour(Direction.RIGHT)).isEqualTo(level.get(1, 0));
    }
}
