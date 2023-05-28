package me.sunstorm.labyrinth.ui.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.jetbrains.annotations.Nullable;

public class NestedObjectProperty<U, T> extends SimpleObjectProperty<T> {
    private final ObjectProperty<U> base;
    private final Callback<U, ObjectProperty<T>> nestedPropertyMapper;
    @Nullable private ObjectProperty<T> nestedProperty;

    public NestedObjectProperty(ObjectProperty<U> base, Callback<U, ObjectProperty<T>> nestedMapper) {
        this.base = base;
        this.nestedPropertyMapper = nestedMapper;
        nestedProperty = nestedPropertyMapper.call(base.get());
        base.addListener((observable, oldValue, newValue) -> {
            nestedProperty = nestedPropertyMapper.call(newValue);
            fireValueChangedEvent();
        });
    }

    @Override
    public void set(T newValue) {
        nestedProperty.set(newValue);
        fireValueChangedEvent();
    }

    @Override
    public T get() {
        return nestedProperty.get();
    }
}
