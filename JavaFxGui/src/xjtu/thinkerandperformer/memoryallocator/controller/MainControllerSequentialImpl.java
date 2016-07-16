package xjtu.thinkerandperformer.memoryallocator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;


public class MainControllerSequentialImpl extends MainController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }


    @Override
    void handleCommand(String command) {
        System.out.println("Sequential Impl: " + command);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Success");
        alert.setContentText("Execute command " + command + "successfully! ");
        addConsoleItem(command, ConsoleCellType.Input);
        addConsoleItem(command + "Executed", ConsoleCellType.MessageOutput);
        alert.show();
    }


}
