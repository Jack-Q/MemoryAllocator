package xjtu.thinkerandperformer.memoryallocator.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FrameController implements Initializable {
    static class SuggestionCellModel {
        private final String value;
        private final String explanation;

        public SuggestionCellModel(String value) {
            this(value, "");
        }


        public SuggestionCellModel(String value, String explanation) {
            this.value = value;
            this.explanation = explanation;
        }

        public String getValue() {
            return value;
        }

        public String getExplanation() {
            return explanation;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @FXML
    public ComboBox<SuggestionCellModel> commandLine;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commandLine.setCellFactory(m -> new ListCell<SuggestionCellModel>() {
            private final TextFlow textFlow;
            private final Text value;
            private final Text explanation;

            {
                setContentDisplay(ContentDisplay.CENTER);
                value = new Text();
                value.setFont(Font.font("Monaco", 1 + Font.getDefault().getSize()));
                value.setFill(Color.web("#fefeee"));
                value.setStyle("-fx-text-fill: #fefeee; -fx-font-weight: 800;");
                explanation = new Text();
                explanation.setStyle("-fx-text-fill: #cccccc; -fx-font-weight: 200; -fx-font-style: italic; -fx-text-alignment: right");
                textFlow = new TextFlow();
                textFlow.getChildren().addAll(value, explanation);
            }

            @Override
            protected void updateItem(SuggestionCellModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    value.setText("12");
                    explanation.setText("32");
                } else {
                    value.setText(item.getValue() + "   ");
                    explanation.setText(item.getExplanation());
                }
                getChildren().clear();
                getChildren().add(textFlow);

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
    }

    private void showPopup() {
        String original = commandLine.getEditor().getText();
        int caretPosition = commandLine.getEditor().getCaretPosition();
        // Fix content and caret position after reset the list of dropdown list
        commandLine.getItems().clear();
        commandLine.getEditor().setText(original);
        commandLine.getEditor().positionCaret(caretPosition);
        commandLine.getItems().addAll(populateSuggestions(original));
        commandLine.show();
    }

    private List<SuggestionCellModel> populateSuggestions(String input) {
        List<SuggestionCellModel> suggestions = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) {
            suggestions.add(new SuggestionCellModel("init", "initialize memory allocator with specified size"));
            suggestions.add(new SuggestionCellModel("new", "create a new variable with specified size"));
            suggestions.add(new SuggestionCellModel("read", "read the content stored in a variable"));
            suggestions.add(new SuggestionCellModel("write", "write content to a defined variable"));
            suggestions.add(new SuggestionCellModel("delete", "delete a defined variable and free its memory space"));
            return suggestions;
        }
        for (int i = 0; i < 10; i++) {
            suggestions.add(new SuggestionCellModel(input + i));
        }
        return suggestions;
    }
}
