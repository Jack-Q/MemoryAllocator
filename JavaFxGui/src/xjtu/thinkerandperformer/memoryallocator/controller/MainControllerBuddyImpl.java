package xjtu.thinkerandperformer.memoryallocator.controller;

import xjtu.thinkerandperformer.memoryallocator.component.VisualBuddyMemManager;
import xjtu.thinkerandperformer.memoryallocator.component.VisualMemManager;

import java.net.URL;
import java.util.ResourceBundle;


public class MainControllerBuddyImpl extends MainController {

    private final VisualBuddyMemManager visualBuddyMemManager = new VisualBuddyMemManager();

    @Override
    VisualMemManager getMemoryManager() {
        return visualBuddyMemManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Connect UI to visual memory manager
        visualBuddyMemManager.setReadValueListener(s -> addConsoleItem(s, ConsoleCellType.DataOutput));

        // hide menu item
        getSequentialMethodSelection().setVisible(false);

    }


}
