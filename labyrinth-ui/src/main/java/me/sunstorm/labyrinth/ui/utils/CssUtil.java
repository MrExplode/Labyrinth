package me.sunstorm.labyrinth.ui.utils;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CssUtil {
    public void addGlobals(List<String> stylesheets) {
        stylesheets.add("/styles-compiled/globals.css");
        stylesheets.add("/styles-compiled/general/padding.css");
        stylesheets.add("/styles-compiled/general/tile.css");
        stylesheets.add("/styles-compiled/general/text.css");
    }
}
