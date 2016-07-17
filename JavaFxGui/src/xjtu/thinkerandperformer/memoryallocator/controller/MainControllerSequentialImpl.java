package xjtu.thinkerandperformer.memoryallocator.controller;

import javafx.beans.property.ReadOnlyObjectProperty;
import xjtu.thinkerandperformer.memoryallocator.component.VisualMemManager;
import xjtu.thinkerandperformer.memoryallocator.component.VisualSequentialMemManager;

import java.net.URL;
import java.util.ResourceBundle;


public class MainControllerSequentialImpl extends MainController {

    private VisualSequentialMemManager visualSequentialMemManager = new VisualSequentialMemManager();

    @Override
    VisualMemManager getMemoryManager() {
        return visualSequentialMemManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // bind listener to method selection

        getSequentialMethodSelection().getSelectionModel().selectedItemProperty().addListener(e ->
                selectMethod(((ReadOnlyObjectProperty<String>) e).getValue()));

        getSequentialMethodSelection().getSelectionModel().selectFirst();
    }

    private void selectMethod(String methodName) {
        visualSequentialMemManager.setFitMethod(methodName);
    }
}
