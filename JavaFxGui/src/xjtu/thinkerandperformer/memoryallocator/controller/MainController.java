package xjtu.thinkerandperformer.memoryallocator.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import xjtu.thinkerandperformer.memoryallocator.algorithm.Variable;
import xjtu.thinkerandperformer.memoryallocator.algorithm.command.ICommand;
import xjtu.thinkerandperformer.memoryallocator.algorithm.command.Parser;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.IllegalCommandException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.MemoryManagerException;
import xjtu.thinkerandperformer.memoryallocator.component.VisualMemManager;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

abstract class MainController implements Initializable {
    @FXML
    public Slider scaleFactorView;
    @FXML
    public ToggleButton viewModeToggle;
    @FXML
    public ListView<AllocationBlockListCell> allocationBlockView;
    @FXML
    public ListView<VariableListCell> variableListView;
    @FXML
    public ListView<ActionHistoryCell> actionHistoryView;
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

    abstract VisualMemManager getMemoryManager();


    enum ConsoleCellType {
        Input, MessageOutput, ErrorOutput, DataOutput
    }

    private static class CommandConsoleCell {
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

    private static class ActionHistoryCell {
        private final Date date = new Date();
        private final String actionName;
        private final String actionArgs;

        ActionHistoryCell(String actionName, String actionArgs) {
            this.actionName = actionName;
            this.actionArgs = actionArgs;
        }

        @Override
        public String toString() {
            return String.format(" %s %s  %tr", actionName, actionArgs, date);
        }
    }

    private static class VariableListCell {
        private final String variableName;
        private final int size;
        private final int position;

        public VariableListCell(String variableName, int size, int position) {
            this.variableName = variableName;
            this.size = size;
            this.position = position;
        }

        public String getVariableName() {
            return variableName;
        }

        public int getSize() {
            return size;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return String.format("%8s %4d %4d", variableName, size, position);
        }
    }

    private enum BlockStatus {
        Busy, Free
    }

    private static class AllocationBlockListCell {
        private final int startPos;
        private final int size;
        private final int prevPosition;
        private final int nextPosition;
        private final BlockStatus status;
        /**
         * Some extra information of a block such as the name of the variable of a node
         */
        private final String extraInfo;

        public AllocationBlockListCell(int startPos, int size, int prevPosition, int nextPosition, BlockStatus status, String extraInfo) {
            this.startPos = startPos;
            this.size = size;
            this.prevPosition = prevPosition;
            this.nextPosition = nextPosition;
            this.status = status;
            this.extraInfo = extraInfo;
        }

        public int getStartPos() {
            return startPos;
        }

        public int getSize() {
            return size;
        }

        public int getPrevPosition() {
            return prevPosition;
        }

        public int getNextPosition() {
            return nextPosition;
        }

        public BlockStatus getStatus() {
            return status;
        }

        public String getExtraInfo() {
            return extraInfo;
        }

        @Override
        public String toString() {
            return String.format("%4d %4d %4d %4d %4s %8s", startPos, size, prevPosition, nextPosition, status.toString(), extraInfo);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind scale setter and indicator
        scaleFactorView.valueProperty().addListener(e -> {
            inspectorMemoryCanvasController.setScaleFactor(scaleFactorView.getValue() / 100);
            scaleMemoryCanvasController.setScaleFactor(scaleFactorView.getValue() / 100);
        });
        scaleMemoryCanvasController.setScaleChangeListener(s -> scaleFactorView.setValue(s * 100));


    }

    void addConsoleItem(String message, ConsoleCellType type) {
        if (type == null) type = ConsoleCellType.MessageOutput;
        commandConsoleView.getItems().add(new CommandConsoleCell(message, type));
    }

    void handleCommand(String command) {

        ICommand parsedCommand;


        // Show input
        addConsoleItem(command, ConsoleCellType.Input);

        // Try Parse Command
        try {
            parsedCommand = Parser.parse(command);
        } catch (IllegalCommandException e) {
            showMessageBox("Malformed command", e.getMessage(), Alert.AlertType.ERROR);
            addConsoleItem(e.getMessage(), ConsoleCellType.ErrorOutput);
            return;
        }

        // Try Execute Command
        try {
            parsedCommand.invoke(getMemoryManager());
        } catch (MemoryManagerException e) {
            showMessageBox("Filed to execute command", e.getMessage(), Alert.AlertType.ERROR);
            addConsoleItem(e.getMessage(), ConsoleCellType.ErrorOutput);
            return;
        }

        showMessageBox("Success", "Successfully executed the command: " + command, Alert.AlertType.INFORMATION);
        // Finished Command, add command to history
        actionHistoryView.getItems().add(new ActionHistoryCell(parsedCommand.getName(), command.trim().replaceFirst(parsedCommand.getName(), "")));

        // Update the variable list
        updateVariableList();
        updateBlockList();
        updateCanvas();
    }

    private void updateBlockList() {
        allocationBlockView.getItems().clear();
        allocationBlockView.getItems().setAll(getMemoryManager().getBlockInfoList().stream().map(i ->
                new AllocationBlockListCell(
                        i.getStartPos(), i
                        .getSize(),
                        i.getPrevPosition(),
                        i.getNextPosition(),
                        i.isFree() ? BlockStatus.Free : BlockStatus.Busy,
                        i.getVariableName()
                )
        ).collect(Collectors.toList()));
    }

    private void updateVariableList() {
        Map<String, Variable> variableMap = getMemoryManager().getVariableMap();
        List<VariableListCell> variableListCells = variableMap.entrySet().stream()
                .map(e -> new VariableListCell(
                        e.getKey(),
                        e.getValue().getHandle().getPos(),
                        getMemoryManager().getBlockSize(e.getValue().getHandle().getPos()))
                ).collect(Collectors.toList());
        variableListView.getItems().clear();
        variableListView.getItems().addAll(variableListCells);
    }

    private static void showMessageBox(String message, String description, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(message);
        alert.setContentText(description);
        alert.setHeaderText(message);
        alert.show();
    }

    private void updateCanvas() {
        inspectorMemoryCanvasController.setBitBlockInformationList(getMemoryManager().getBitBlockInformationList());
        inspectorMemoryCanvasController.setBlockCount(getMemoryManager().getBlockCount());


        scaleMemoryCanvasController.setBitBlockInformationList(getMemoryManager().getBitBlockInformationList());
        scaleMemoryCanvasController.setBlockCount(getMemoryManager().getBlockCount());
    }
}
