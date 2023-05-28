package me.sunstorm.labyrinth.core.inject;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Predicate;

/**
 * A simplistic dependency injection system designed primarily for JavaFX controller instantiation.
 * For the sake of simplicity, every provided dependency is handled as a singleton.
 * <p> Dependencies can be registered in two ways: simple providers, and dependency factories.
 *
 * <h2>Simple providers</h2>
 * Simple string dependency:
 * {@snippet :
 *      Injector injector = new Injector();
 *      injector.addSimpleProvider(String.class, () -> "hello!");
 *}
 * String dependency with a named qualifier:
 * {@snippet :
 *      Injector injector = new Injector();
 *      injector.addSimpleProvider(String.class, "qualifier-1", () -> "I'm a named dependency!");
 *}
 *
 * <h2>Dependency factories</h2>
 * The more versatile way of making providers is using dependency factories.
 * This enables the possibility of creating nested dependencies.
 *
 */
@Slf4j
@SuppressWarnings("unchecked")
public class Injector {
    private final Predicate<Constructor<?>> CONSTRUCTOR_PREDICATE = c -> c.getParameterCount() != 0 && c.isAnnotationPresent(Inject.class);
    private final Map<DependencyKey, Provider<?>> providerMap = new HashMap<>();
    private final Map<DependencyKey, Object> instanceMap = new HashMap<>();

    /**
     * Creates a new injector.
     */
    public Injector() {
        providerMap.put(new DependencyKey(Injector.class, null), () -> this);
    }

    /**
     * Register a provider factory object.
     * @param providerFactory the factory
     */
    public void addProviderFactory(Object providerFactory) {
        var providerMethods = Arrays.stream(providerFactory.getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Provides.class)).toList();
        for (var m : providerMethods) {
            if (m.getParameterCount() == 0)
                generateSimpleProvider(providerFactory, m);
            else
                generateRecursiveProvider(providerFactory, m);
        }
    }

    private void generateSimpleProvider(Object o, Method m) {
        var type = m.getReturnType();
        var name = m.isAnnotationPresent(Named.class) ? m.getAnnotation(Named.class).value() : null;
        providerMap.put(new DependencyKey(type, name), () -> {
            try {
                m.setAccessible(true);
                return m.invoke(o);
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException("Failed to invoke provider method", e);
            }
        });
    }

    private void generateRecursiveProvider(Object o, Method m) {
        var type = m.getReturnType();
        var name = m.isAnnotationPresent(Named.class) ? m.getAnnotation(Named.class).value() : null;
        providerMap.put(new DependencyKey(type, name), () -> {
            try {
                List<Object> instances = new ArrayList<>();
                for (var p : m.getParameters()) {
                    instances.add(getProvidedInstance(resolveParameterKey(p)));
                }
                m.setAccessible(true);
                return m.invoke(o, instances.toArray(Object[]::new));
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException("Failed to invoke provider method", e);
            }
        });
    }

    /**
     * Register a simple provider for a type.
     * All injection candidates of this type without further qualifiers will receive this provider.
     * @param type the type of the provided dependency
     * @param provider the provider
     * @param <T> the type of the provided dependency
     */
    public <T> void addSimpleProvider(Class<T> type, Provider<T> provider) {
        addSimpleProvider(type, null, provider);
    }

    /**
     * Register a simple provider with a name qualifier for a type.
     *
     * @param type the type of the provided dependency
     * @param name the name qualifier
     * @param <T> the type of the provided dependency
     * @param provider the provider
     */
    public <T> void addSimpleProvider(Class<T> type, String name, Provider<T> provider) {
        providerMap.put(new DependencyKey(type, name), provider);
    }

    /**
     *
     * @param from the type of the needed instance
     * @param <T> the type of the needed instance
     * @return the created instance
     */
    public <T> T createInstance(Class<T> from) {
        try {
            var constructor = findEligableConstructor(from);
            constructor.setAccessible(true);
            if (constructor.getParameterCount() == 0) return (T) constructor.newInstance();
            List<Object> parameterInstances = new ArrayList<>();
            for (var p : constructor.getParameters()) {
                var key = resolveParameterKey(p);
                if (!providerMap.containsKey(key)) throw new IllegalArgumentException("No provider found for key: " + key);
                parameterInstances.add(getProvidedInstance(key));
            }
            return (T) constructor.newInstance(parameterInstances.toArray(Object[]::new));
        } catch (ReflectiveOperationException e) {
            log.error("Failed to create instance for " + from.getName(), e);
            return null;
        }
    }

    /**
     * Tries to retrieve an instance matching the key, or computing an instance from an existing but not yet computed provider.
     * <p>This really shouldn't be used anywhere, I just needed this small hack for accessing the global state in LabyrinthScene transition.
     * @param key the dependency key
     * @return the object resolved from providers matching the key
     */
    public Object getProvidedInstance(DependencyKey key) {
        // computeIfAbsent throws CME (I know this is not the good way to fix it)
        if (!instanceMap.containsKey(key)) {
            var provider = providerMap.get(key);
            var instance = provider.get();
            instanceMap.put(key, instance);
        }
        return instanceMap.get(key);
        //return instanceMap.computeIfAbsent(key, k -> providerMap.get(k).get());
    }

    private Constructor<?> findEligableConstructor(Class<?> in) {
        try {
            var candidate = Arrays.stream(in.getConstructors()).filter(CONSTRUCTOR_PREDICATE).findFirst().orElse(null);
            return candidate == null ? in.getDeclaredConstructor() : candidate;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No constructor matching injection criteria found", e);
        }
    }

    private DependencyKey resolveParameterKey(Parameter parameter) {
        var type = parameter.getType();
        var name = parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
        return new DependencyKey(type, name);
    }
}
