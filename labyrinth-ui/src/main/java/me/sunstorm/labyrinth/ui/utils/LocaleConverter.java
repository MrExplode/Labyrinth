package me.sunstorm.labyrinth.ui.utils;

import javafx.util.StringConverter;

import java.util.Locale;

public class LocaleConverter extends StringConverter<Locale> {

    @Override
    public String toString(Locale lang) {
        return lang == null ? null : lang.toString();
    }

    @Override
    public Locale fromString(String string) {
        return string == null ? null : Locale.of(string);
    }
}
