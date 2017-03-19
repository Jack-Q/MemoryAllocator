package xjtu.thinkerandperformer.memoryallocator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MemoryAllocator extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL resource = getClass().getClassLoader().getResource("view/Frame.fxml");
        if(resource == null){
            System.out.print("Cannot find main view, which is required for this program");
            return;
        }
        Parent root = FXMLLoader.load(resource);
        primaryStage.setTitle("Memory Allocation Visualization");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
