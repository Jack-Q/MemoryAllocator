package xjtu.thinkerandperformer.memoryallocator.controller;


import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
    public TableView<AllocationBlockListCell> allocationBlockView;
    @FXML
    public TableView<VariableListCell> variableListView;
    @FXML
    public TableView<ActionHistoryCell> actionHistoryView;
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

    @FXML
    private ChoiceBox<String> sequentialMethodSelection;

    private static void showMessageBox(String message, String description, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(message);
        alert.setContentText(description);
        alert.setHeaderText(message);
        alert.show();
    }

    public ChoiceBox<String> getSequentialMethodSelection() {
        return sequentialMethodSelection;
    }

    abstract VisualMemManager getMemoryManager();

    List<String> getVariableList() {
        return getMemoryManager().getVariableList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind scale setter and indicator
        scaleFactorView.valueProperty().addListener(e -> {
            inspectorMemoryCanvasController.setScaleFactor(scaleFactorView.getValue() / 100);
            scaleMemoryCanvasController.setScaleFactor(scaleFactorView.getValue() / 100);
        });
        scaleMemoryCanvasController.setScaleChangeListener(s -> scaleFactorView.setValue(s * 100));

        // Setup block table view control
        TableColumn<AllocationBlockListCell, Integer> startPosColumn = new TableColumn<>("Start Position");
        startPosColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getStartPos()));
        TableColumn<AllocationBlockListCell, Integer> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getSize()));
        TableColumn<AllocationBlockListCell, Integer> prevPosColumn = new TableColumn<>("Prev Position");
        prevPosColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getPrevPosition()));
        TableColumn<AllocationBlockListCell, Integer> nextPosColumn = new TableColumn<>("Next Position");
        nextPosColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getNextPosition()));
        TableColumn<AllocationBlockListCell, BlockStatus> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getStatus()));
        TableColumn<AllocationBlockListCell, String> extraInfoColumn = new TableColumn<>("Extra Info");
        extraInfoColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getExtraInfo()));
        allocationBlockView.getColumns().addAll(startPosColumn, sizeColumn, prevPosColumn, nextPosColumn, statusColumn, extraInfoColumn);

        // Setup variable table view control
        TableColumn<VariableListCell, String> variableNameColumn = new TableColumn<>("Name");
        variableNameColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getVariableName()));
        TableColumn<VariableListCell, Integer> variableSizeColumn = new TableColumn<>("Size");
        variableSizeColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getSize()));
        TableColumn<VariableListCell, Integer> variablePositionColumn = new TableColumn<>("Position");
        variablePositionColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getPosition()));
        variableListView.getColumns().setAll(variableNameColumn, variableSizeColumn, variablePositionColumn);
        variableListView.setRowFactory(p -> {
            final TableRow<VariableListCell> tableRow = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().addAll(new MenuItem("read"), new MenuItem("write"), new MenuItem("delete"));
            contextMenu.setOnAction(a -> {
                MenuItem item = (MenuItem) a.getTarget();
                String varName = tableRow.getItem().getVariableName();
                switch (item.getText()) {
                    case "read":
                        handleCommand("read " + varName);
                        break;

                    case "write":

                        TextInputDialog textInputDialog = new TextInputDialog();
                        textInputDialog.setHeaderText("Edit Variable Content");
                        textInputDialog.setTitle("Edit variable content");
                        textInputDialog.setContentText("Set the value of " + varName + " to:");
                        textInputDialog.showAndWait().ifPresent(s -> handleCommand("write " + varName + " = \"" + s + "\""));
                        break;

                    case "delete":
                        handleCommand("delete " + varName);
                        break;
                }

            });

            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            tableRow.contextMenuProperty().bind(
                    Bindings.when(tableRow.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return tableRow;
        });

        // Setup action history view control
        TableColumn<ActionHistoryCell, String> actionNameColumn = new TableColumn<>("Action");
        actionNameColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getActionName()));
        TableColumn<ActionHistoryCell, String> actionArgsColumn = new TableColumn<>("Arguments");
        actionArgsColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getActionArgs()));
        TableColumn<ActionHistoryCell, Date> actionTimeColumn = new TableColumn<>("Time");
        actionTimeColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getDate()));
        actionHistoryView.getColumns().addAll(actionNameColumn, actionArgsColumn, actionTimeColumn);
        actionHistoryView.setRowFactory(p -> {
            final TableRow<ActionHistoryCell> tableRow = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().addAll(new MenuItem("copy command"), new MenuItem("execute"));
            contextMenu.setOnAction(a -> {
                MenuItem item = (MenuItem) a.getTarget();
                String text = item.getText();
                String action = tableRow.getItem().getActionName() + " " + tableRow.getItem().getActionArgs();
                switch (text) {
                    case "copy command":
                        ClipboardContent clipboardContent = new ClipboardContent();
                        clipboardContent.putString(action);
                        Clipboard.getSystemClipboard().setContent(clipboardContent);
                        break;

                    case "execute":
                        handleCommand(action);
                        break;
                }

            });
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            tableRow.contextMenuProperty().bind(
                    Bindings.when(tableRow.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return tableRow;
        });

        commandConsoleView.setCellFactory(p -> {
            ListCell<CommandConsoleCell> cell = new ListCell<>();
            cell.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) cell.setText(newValue.getContent());
                if (oldValue != null && newValue != null && oldValue.getConsoleCellType() == newValue.getConsoleCellType())
                    return;
                if (oldValue != null) {
                    cell.getStyleClass().removeIf(s -> s.equals(oldValue.getConsoleCellType().toString()));

                }
                if (newValue != null) {
                    cell.getStyleClass().add(newValue.getConsoleCellType().toString());
                }
            });
            return cell;
        });

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
        actionHistoryView.getItems().add(new ActionHistoryCell(parsedCommand.getName(), command.trim().replaceFirst(parsedCommand.getName(), "").trim()));

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

    private void updateCanvas() {
        inspectorMemoryCanvasController.setBitBlockInformationList(getMemoryManager().getBitBlockInformationList());
        inspectorMemoryCanvasController.setBlockCount(getMemoryManager().getBlockCount());


        scaleMemoryCanvasController.setBitBlockInformationList(getMemoryManager().getBitBlockInformationList());
        scaleMemoryCanvasController.setBlockCount(getMemoryManager().getBlockCount());
    }

    enum ConsoleCellType {
        Input, MessageOutput, ErrorOutput, DataOutput
    }

    private enum BlockStatus {
        Busy, Free
    }

    private static class CommandConsoleCell {
        private final String content;
        private final ConsoleCellType consoleCellType;

        public CommandConsoleCell(String content, ConsoleCellType type) {
            this.content = content;
            this.consoleCellType = type;
        }

        public String getContent() {
            return content;
        }

        public ConsoleCellType getConsoleCellType() {
            return consoleCellType;
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

        public Date getDate() {
            return date;
        }

        public String getActionName() {
            return actionName;
        }

        public String getActionArgs() {
            return actionArgs;
        }
    }

    private static class VariableListCell {
        private final String variableName;
        private final int size;
        private final int position;

        public VariableListCell(String variableName, int position, int size) {
            this.variableName = variableName;
            this.position = position;
            this.size = size;
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
}
