package me.sunstorm.labyrinth.core.inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.*;

public class GraphInjectorTests {
    private Injector injector;

    @BeforeEach
    void setup() {
        injector = new Injector();
    }

    private static class DependencyRoot {
        private final String value;

        public DependencyRoot(String value) {
            this.value = value;
        }
    }

    private static class DependencyNode {
        private final DependencyRoot dr;

        public DependencyNode(DependencyRoot dr) {
            this.dr = dr;
        }
    }

    private static class RecursiveInjectionSubject {
        private final DependencyNode dn;

        @Inject
        public RecursiveInjectionSubject(DependencyNode md) {
            this.dn = md;
        }
    }

    @Test
    void testRecursive() {
        String value = "fancy stuff";
        injector.addProviderFactory(new Object() {
            @Provides
            DependencyNode provideNode(DependencyRoot root) {
                return new DependencyNode(root);
            }

            @Provides
            DependencyRoot provideRoot() {
                return new DependencyRoot(value);
            }
        });
        var instance = injector.createInstance(RecursiveInjectionSubject.class);
        assertThat(instance.dn.dr.value).isEqualTo(value);
    }
}
