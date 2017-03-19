package xjtu.thinkerandperformer.memoryallocator.controller;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class FrameController implements Initializable {
    public AnchorPane sequentialMethodPanel;
    public AnchorPane buddyMethodPanel;
    public Button executeButton;
    public TabPane methodTabs;
    public ContextMenu commandLineContextMenu;
    private MainController sequentialMethodPanelController;
    private MainController buddyMethodPanelController;

    private MainController currentController() {

        Tab tab = methodTabs.getTabs().stream().filter(Tab::isSelected).findFirst().orElse(null);
        switch (tab.getUserData().toString()) {
            case "sequential":
                return sequentialMethodPanelController;

            case "buddy":
                return buddyMethodPanelController;

        }
        return sequentialMethodPanelController;
    }


    private static class SuggestionCellModel {
        private final String value;
        private final String explanation;

        SuggestionCellModel(String value) {
            this(value, "");
        }


        SuggestionCellModel(String value, String explanation) {
            this.value = value;
            this.explanation = explanation;
        }

        String getValue() {
            return value;
        }

        String getExplanation() {
            return explanation;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SuggestionCellModel that = (SuggestionCellModel) o;

            return value.equals(that.value);

        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    @FXML
    public ComboBox<SuggestionCellModel> commandLine;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load main views
        try {
            FXMLLoader mainFxmlLoader;
            ClassLoader classLoader = this.getClass().getClassLoader();

            mainFxmlLoader = new FXMLLoader(classLoader.getResource("view/Main.fxml"));
            mainFxmlLoader.setController(sequentialMethodPanelController = new MainControllerSequentialImpl());
            sequentialMethodPanel.getChildren().add(mainFxmlLoader.load());
            mainFxmlLoader = new FXMLLoader(classLoader.getResource("view/Main.fxml"));
            mainFxmlLoader.setController(buddyMethodPanelController = new MainControllerBuddyImpl());
            buddyMethodPanel.getChildren().add(mainFxmlLoader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Initialize command line candidates filler
        commandLine.setCellFactory(m -> new ListCell<SuggestionCellModel>() {
            private final TextFlow textFlow;
            private final Text value;
            private final Text explanation;

            // Initialize Prompt View Template
            {
                setContentDisplay(ContentDisplay.CENTER);
                value = new Text();
                value.setFont(Font.font("Monaco", 1 + Font.getDefault().getSize()));
                value.setStyle("-fx-text-fill: #fefeee; /*noinspection CssUnitlessNumber*/-fx-font-weight: 800;");
                value.applyCss();
                explanation = new Text();
                explanation.setStyle("-fx-text-fill: #cccccc; /*noinspection CssUnitlessNumber*/-fx-font-weight: 200; -fx-font-style: italic; -fx-text-alignment: right");
                explanation.applyCss();
                textFlow = new TextFlow();
                textFlow.getChildren().addAll(value, explanation);
                textFlow.applyCss();
            }

            @Override
            protected void updateItem(SuggestionCellModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    value.setText("");
                    explanation.setText("");
                } else {
                    value.setText(item.getValue() + "   ");
                    explanation.setText(item.getExplanation());
                }
                this.getChildren().clear();
                this.getChildren().add(textFlow);
                this.applyCss();
            }
        });
        commandLine.setConverter(new StringConverter<SuggestionCellModel>() {
            @Override
            public String toString(SuggestionCellModel object) {
                return object == null ? "" : object.getValue();
            }

            @Override
            public SuggestionCellModel fromString(String string) {
                return new SuggestionCellModel(string);
            }
        });
        commandLine.focusedProperty().addListener((f, o, n) -> {
            if (n) {
                showPopup();
            }
        });
        commandLine.getEditor().setOnKeyReleased(e -> {
            if (e.getCode().isArrowKey()) return;
            e.consume();
            showPopup();
        });
        executeButton.setOnAction(e -> {
            currentController().handleCommand(commandLine.getValue() == null ? commandLine.getEditor().getText() : commandLine.getValue().getValue());
            commandLine.setValue(new SuggestionCellModel(""));
        });
        commandLine.getEditor().setContextMenu(commandLine.getContextMenu());
        commandLineContextMenu.setOnShowing(e -> commandLineContextMenu.getItems().stream()
                .filter(m -> m instanceof Menu)
                .forEach(m -> {
                            ObservableList<MenuItem> items = ((Menu) m).getItems();
                            items.clear();
                            items.addAll(
                                    currentController().getVariableList().stream()
                                            .map(MenuItem::new)
                                            .collect(Collectors.toList())
                            );
                            if (items.size() == 0) {
                                final MenuItem defaultItem = new MenuItem("no variable now");
                                defaultItem.setDisable(true);
                                items.add(defaultItem);
                            }
                        }
                )
        );
        final EventHandler<ActionEvent> menuEventHandler = a -> {
            MenuItem targetMenuItem = (MenuItem) a.getTarget();
            String parentNodeText = targetMenuItem.getParentMenu() != null ? targetMenuItem.getParentMenu().getText() : "";
            String currentNodeText = targetMenuItem.getText();

            String command = (parentNodeText != null ? parentNodeText + " " : "") + currentNodeText + " ";
            commandLine.getEditor().setText(command);
            commandLine.getEditor().positionCaret(command.length());
        };
        commandLineContextMenu.getItems().forEach(m -> m.setOnAction(menuEventHandler));
    }

    private void showPopup() {
        String original = commandLine.getEditor().getText();
        int caretPosition = commandLine.getEditor().getCaretPosition();
        // Fix content and caret position after reset the list of dropdown list
        commandLine.getItems().clear();
        commandLine.getEditor().setText(original);
        commandLine.getEditor().positionCaret(caretPosition);
        commandLine.getItems().addAll(populateSuggestions(original));
        commandLine.applyCss();
        commandLine.show();
    }


    private final List<SuggestionCellModel> defaultSuggestion = new ArrayList<>();

    {
        defaultSuggestion.add(new SuggestionCellModel("init", "initialize memory allocator with specified size"));
        defaultSuggestion.add(new SuggestionCellModel("new", "create a new variable with specified size"));
        defaultSuggestion.add(new SuggestionCellModel("read", "read the content stored in a variable"));
        defaultSuggestion.add(new SuggestionCellModel("write", "write content to a defined variable"));
        defaultSuggestion.add(new SuggestionCellModel("delete", "delete a defined variable and free its memory space"));
    }

    private final String[] allTokens = {"init", "new", "read", "write", "delete"};
    private final String[] collectiveTokens = {"read", "write", "delete"};

    private List<SuggestionCellModel> populateSuggestions(String inputRaw) {
        final String input = inputRaw == null ? "" : inputRaw.toLowerCase().trim();
        final List<String> currentVariableList = currentController().getVariableList();
        final List<SuggestionCellModel> suggestions = new ArrayList<>();

        if (input.length() > 0) {
            currentVariableList.stream()
                    .filter(v -> v.contains(input) || input.contains(v))
                    .forEach(v -> Arrays.stream(collectiveTokens)
                            .forEach(token -> suggestions.add(new SuggestionCellModel(token + " " + v)))
                    );

            for (String token : collectiveTokens) {
                if (token.contains(input) || input.startsWith(token))
                    currentVariableList.forEach(v -> suggestions.add(new SuggestionCellModel(token + " " + v)));
            }

            for (String token : collectiveTokens) {
                if (token.contains(input) || input.startsWith(token))
                    currentVariableList.forEach(v -> suggestions.add(new SuggestionCellModel(token + " " + v)));
            }

            for (String token : allTokens) {
                if (token.contains(input) || input.startsWith(token))
                    suggestions.add(new SuggestionCellModel(token));
            }
        }

        // Fill up the suggestion list
        suggestions.addAll(defaultSuggestion);

        return suggestions.stream().distinct().limit(5).collect(Collectors.toList());

    }
}
