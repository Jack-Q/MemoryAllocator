package xjtu.thinkerandperformer.memoryallocator.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class MainController implements Initializable {
    @FXML
    public Slider scaleFactorView;
    @FXML
    public ToggleButton viewModeToggle;
    @FXML
    public ListView<CommandConsoleCell> allocationBlockView;
    @FXML
    public ListView<CommandConsoleCell> variableListView;
    @FXML
    public ListView<CommandConsoleCell> actionHistoryView;
    @FXML
    public ListView<CommandConsoleCell> commandConsoleView;
    @FXML
    private Label inspectorMemoryCanvas;
    @FXML
    private InspectorMemoryCanvasController inspectorMemoryCanvasController;
    @FXML
    private Label scaleMemoryCanvas;
    @FXML
    private ScaleMemoryCanvasController scaleMemoryCanvasController;


    enum ConsoleCellType {
        Input, MessageOutput, ErrorOutput, DataOutput
    }

    static class CommandConsoleCell {
        private final String content;
        private final ConsoleCellType consoleCellType;

        public CommandConsoleCell(String content, ConsoleCellType type) {
            this.content = content;
            this.consoleCellType = type;
        }

        @Override
        public String toString() {
            return content;
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scaleFactorView.valueProperty().addListener(e -> {
            inspectorMemoryCanvasController.setScaleFactor(scaleFactorView.getValue() / 100);
            scaleMemoryCanvasController.setScaleFactor(scaleFactorView.getValue() / 100);
        });
        scaleMemoryCanvasController.setScaleChangeListener(s -> scaleFactorView.setValue(s * 100));
    }

    public void addConsoleItem(String message, ConsoleCellType type) {
        if (type == null) type = ConsoleCellType.MessageOutput;
        commandConsoleView.getItems().add(new CommandConsoleCell(message, type));
    }

    abstract void handleCommand(String command);

}
