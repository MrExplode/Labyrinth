package me.sunstorm.labyrinth.core.game;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.PathType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class GameResultStoreTests {
    private FileSystem dummyFileSystem;
    private GameResultStore store;

    @BeforeEach
    void setup() {
        dummyFileSystem = Jimfs.newFileSystem(Configuration
                .builder(PathType.unix())
                .setRoots("/")
                .setAttributeViews("basic")
                .setWorkingDirectory("/")
                .build());
        store = new GameResultStore(dummyFileSystem.getPath(""));
    }

    @Test
    void testLoadEmpty() throws IOException {
        store.load();
        assertThat(store.getResults()).isEmpty();
    }

    @Test
    void testLoad() throws IOException {
        Files.copy(getClass().getResourceAsStream("/results.json"), dummyFileSystem.getPath("results.json"));
        store.load();
        assertThat(store.getResults().size()).isEqualTo(1);
    }

    @Test
    void testSaveEmpty() throws IOException {
        store.save();
        assertThat(Files.notExists(dummyFileSystem.getPath("results.json"))).isTrue();
    }

    @Test
    void testSave() throws IOException {
        store.addResult(createResult(true, 2, LocalDateTime.of(2023, 5, 30, 1, 0, 0), LocalDateTime.of(2023, 5, 30, 1, 0, 15)));
        store.save();
        assertThat(Files.exists(dummyFileSystem.getPath("results.json"))).isTrue();
    }

    @Test
    void testLatestResult() {
        var res = createResult(true, 2, LocalDateTime.of(2023, 5, 30, 1, 0, 0), LocalDateTime.of(2023, 5, 30, 1, 0, 15));
        store.addResult(createResult(false, 2, LocalDateTime.of(2023, 5, 30, 1, 0, 0), LocalDateTime.of(2023, 5, 30, 1, 0, 15)));
        store.addResult(res);
        assertThat(store.getLatestResult()).isEqualTo(res);
    }

    @Test
    void testResultOrder() {
        var res1 = createResult(true, 2, LocalDateTime.of(2023, 5, 30, 1, 0, 0), LocalDateTime.of(2023, 5, 30, 1, 0, 15));
        var res2 = createResult(true, 2, LocalDateTime.of(2023, 5, 30, 1, 0, 0), LocalDateTime.of(2023, 5, 30, 1, 0, 16));
        var res3 = createResult(true, 5, LocalDateTime.of(2023, 5, 30, 1, 0, 0), LocalDateTime.of(2023, 5, 30, 1, 0, 15));
        var res4 = createResult(false, 2, LocalDateTime.of(2023, 5, 30, 1, 0, 0), LocalDateTime.of(2023, 5, 30, 1, 0, 15));

        store.addResult(res2);
        store.addResult(res4);
        store.addResult(res1);
        store.addResult(res3);

        assertThat(store.getResults()).containsExactlyElementsOf(List.of(res1, res2, res3, res4));
    }

    private GameResult createResult(boolean win, int steps, LocalDateTime start, LocalDateTime stop) {
        var res = new GameResult();
        res.setEscaped(win);
        IntStream.range(0, steps).forEach(__ -> res.addStep());
        res.setStart(start);
        res.setEnd(stop);
        return res;
    }
}
