package me.sunstorm.labyrinth.core.serialization;

import me.sunstorm.labyrinth.core.Constants;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

public class LocaleSerializerTests {

    @Test
    void testLocaleSerialization() {
        var hu = Locale.of("HU");
        var en = Locale.ENGLISH;
        var huSerialized = Constants.GSON.toJson(hu);
        var enSerialized = Constants.GSON.toJson(en);
        assertThat(huSerialized).isEqualTo("\"hu\"");
        assertThat(enSerialized).isEqualTo("\"en\"");
    }

    @Test
    void testLocaleDeserialization() {
        var hu = "HU";
        var en = "EN";
        var huDeserialized = Constants.GSON.fromJson(hu, Locale.class);
        var enDeserialized = Constants.GSON.fromJson(en, Locale.class);
        assertThat(huDeserialized).isEqualTo(Locale.of("HU"));
        assertThat(enDeserialized).isEqualTo(Locale.ENGLISH);
    }
}
