package me.sunstorm.labyrinth.core.level;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.PathType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.*;

public class LevelStoreTests {
    private FileSystem dummyFileSystem;
    private LevelStore store;

    @BeforeEach
    void setup() throws IOException {
        dummyFileSystem = Jimfs.newFileSystem(Configuration
                .builder(PathType.unix())
                .setRoots("/")
                .setAttributeViews("basic")
                .setWorkingDirectory("/")
                .build());
        Files.createDirectories(dummyFileSystem.getPath("levels"));
        store = new LevelStore(dummyFileSystem.getPath("levels"));
    }

    @Test
    void testLoad() throws IOException {
        Files.copy(getClass().getResourceAsStream("/defaultLevel.json"), dummyFileSystem.getPath("levels", "defaultLevel.json"));
        store.load();
        assertThat(store.getLevels()).hasSize(2);
        assertThat(store.getLevels().stream().anyMatch(l -> l.getName().equals("default"))).isTrue();
        assertThat(store.getLevels().stream().anyMatch(l -> l.getName().equals("defaultLevel"))).isTrue();
    }

    @Test
    void testLoadDefault() throws IOException {
        store.load();
        assertThat(store.getLevels()).isNotEmpty();
        assertThat(store.getLevels()).hasSize(1);
        assertThat(store.getLevels().get(0).getName()).isEqualTo("default");
    }

    @Test
    void testSave() throws IOException {
        store.load();
        var other = store.createEmpty();
        other.nameProperty().set("otherLevel");
        store.getLevels().add(other);
        store.save();
        assertThat(Files.exists(dummyFileSystem.getPath("levels", "default.json"))).isTrue();
        assertThat(Files.exists(dummyFileSystem.getPath("levels", "otherLevel.json"))).isTrue();
    }

    @Test
    void testLevelCleanup() throws IOException {
        Files.copy(getClass().getResourceAsStream("/defaultLevel.json"), dummyFileSystem.getPath("levels", "defaultLevel.json"));
        store.save();
        assertThat(Files.notExists(dummyFileSystem.getPath("levels", "defaultLevel.json"))).isTrue();
    }
}
