package xjtu.thinkerandperformer.memoryallocator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MemoryAllocator extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("View/ScaleMemoryCanvas.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
