package xjtu.thinkerandperformer.memoryallocator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;


public class MainControllerBuddyImpl extends MainController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    @Override
    void handleCommand(String command) {
        System.out.println("Buddy Impl: " + command);
        addConsoleItem(command, ConsoleCellType.Input);
        addConsoleItem("Executed command: " + command, ConsoleCellType.MessageOutput);
    }

}
