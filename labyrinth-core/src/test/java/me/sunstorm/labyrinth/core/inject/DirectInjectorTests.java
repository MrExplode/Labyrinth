package me.sunstorm.labyrinth.core.inject;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DirectInjectorTests {
    private Injector injector;

    @BeforeEach
    void setup() {
        injector = new Injector();
    }

    private static class InvalidConstructorSubject {
        public InvalidConstructorSubject(String unusedParameter) {}
    }

    @Test
    void testInvalidConstructorException() {
        assertThatThrownBy(() -> injector.createInstance(InvalidConstructorSubject.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("No constructor matching injection criteria found");
    }

    @Getter
    private static class BasicInjectionSubject {
        private final String field;

        @Inject
        public BasicInjectionSubject(String field) {
            this.field = field;
        }
    }

    @Test
    void testNoProvider() {
        assertThatThrownBy(() -> injector.createInstance(BasicInjectionSubject.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("No provider found for key:");
    }

    @Test
    void testNoProviderKey() {
        injector.addSimpleProvider(String.class, "key1", () -> "value");
        assertThatThrownBy(() -> injector.createInstance(BasicInjectionSubject.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("No provider found for key:");
    }

    @Test
    void testInjectInstance() {
        String injected = "some string";
        injector.addSimpleProvider(String.class, () -> injected);
        var instance = injector.createInstance(BasicInjectionSubject.class);
        assertThat(instance.getField()).isEqualTo("some string");
        assertThat(instance.getField()).isEqualTo(injected);
    }

    @Getter
    private static class KeyedInjectionSubject {
        private final String field1;
        private final String field2;

        @Inject
        public KeyedInjectionSubject(@Named("key1") String field1, @Named("key2") String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    @Test
    void testDistinctKeying() {
        String field1 = "first string";
        String field2 = "second string";
        injector.addSimpleProvider(String.class, "key1", () -> field1);
        injector.addSimpleProvider(String.class, "key2", () -> field2);
        var instance = injector.createInstance(KeyedInjectionSubject.class);
        assertThat(instance.getField1()).isEqualTo("first string");
        assertThat(instance.getField1()).isEqualTo(field1);
        assertThat(instance.getField2()).isEqualTo("second string");
        assertThat(instance.getField2()).isEqualTo(field2);
    }

    @Getter
    private static class MixedInjectionSubject {
        private final String field1;
        private final String field2;

        @Inject
        public MixedInjectionSubject(@Named("key1") String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    @Test
    void testMixedKeying() {
        String field1 = "keyed instance";
        String field2 = "default instance";
        injector.addSimpleProvider(String.class, "key1", () -> field1);
        injector.addSimpleProvider(String.class, () -> field2);
        var instance = injector.createInstance(MixedInjectionSubject.class);
        assertThat(instance.getField1()).isEqualTo("keyed instance");
        assertThat(instance.getField1()).isEqualTo(field1);
        assertThat(instance.getField2()).isEqualTo("default instance");
        assertThat(instance.getField2()).isEqualTo(field2);
    }
}
