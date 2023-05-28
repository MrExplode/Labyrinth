package me.sunstorm.labyrinth.ui;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bootstrap {

    public static void main(String[] args) {
        log.info("Hello from Labyrinth!");
        Application.launch(LabyrinthApplication.class, args);
    }
}
