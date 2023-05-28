package me.sunstorm.labyrinth.ui.utils;

import javafx.scene.control.Alert;
import lombok.experimental.UtilityClass;

import java.io.PrintWriter;
import java.io.StringWriter;

@UtilityClass
public class AlertUtil {
    public void showExceptionError(String message, Throwable e) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        var writer = new StringWriter();
        var printer = new PrintWriter(writer);
        cause(e).printStackTrace(printer);
        alert.setContentText(writer.toString().substring(0, 1000));
        alert.show();
    }

    private Throwable cause(Throwable t) {
        if (t.getCause() == null) return t;
        return cause(t.getCause());
    }
}
