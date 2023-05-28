package me.sunstorm.labyrinth.core.entity;

import me.sunstorm.labyrinth.core.Position;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class EntityTests {

    @Test
    void testPlayerPosition() {
        var position = Position.of(12, 85);
        var player = new Player(position);
        assertThat(player.getPosition()).isEqualTo(position);
    }

    @Test
    void testMonsterPosition() {
        var position = Position.of(6, 9);
        var monster = new Monster(position);
        assertThat(monster.getPosition()).isEqualTo(position);
    }
}
