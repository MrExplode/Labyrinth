package me.sunstorm.labyrinth.core.utils;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.PathType;
import me.sunstorm.labyrinth.core.level.Level;
import me.sunstorm.labyrinth.core.settings.LabyrinthSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import static org.assertj.core.api.Assertions.*;

public class JsonUtilTests {
    private FileSystem dummyFileSystem;

    @BeforeEach
    void setup() throws IOException {
        dummyFileSystem = Jimfs.newFileSystem(Configuration
                .builder(PathType.unix())
                .setRoots("/")
                .setAttributeViews("basic")
                .setWorkingDirectory("/")
                .build());
        Files.copy(getClass().getResourceAsStream("/defaultLevel.json"), dummyFileSystem.getPath("defaultLevel.json"));
    }

    @Test
    void testMissingFile() {
        assertThatThrownBy(() -> JsonUtils.load(LabyrinthSettings.class, dummyFileSystem.getPath("notExistingFile.json"))).isInstanceOf(NoSuchFileException.class);
    }

    @Test
    void testMissingOptional() {
        var loadedOptional = JsonUtils.loadOptional(Level.class, dummyFileSystem.getPath("notExistingFile.json"));
        assertThat(loadedOptional).isEmpty();
    }

    @Test
    void testSuccessfulLoad() {
        var loadedOptional = JsonUtils.loadOptional(Level.class, dummyFileSystem.getPath("defaultLevel.json"));
        assertThat(loadedOptional).isNotEmpty();
    }

    @Test
    void testSave() throws IOException {
        var data = new LabyrinthSettings();
        var path = dummyFileSystem.getPath("settings.json");
        JsonUtils.save(data, path);
        assertThat(Files.exists(path)).isTrue();
    }

    @Test
    void testSilentSave() {
        var data = new LabyrinthSettings();
        var path = dummyFileSystem.getPath("settings.json");
        JsonUtils.saveSilent(data, path);
        assertThat(Files.exists(path)).isTrue();
    }

    @Test
    void testSilentSaveFail() {
        var data = new LabyrinthSettings();
        var path = dummyFileSystem.getPath("folder/settings.json");
        JsonUtils.saveSilent(data, path);
        assertThat(Files.notExists(path)).isTrue();
    }
}
