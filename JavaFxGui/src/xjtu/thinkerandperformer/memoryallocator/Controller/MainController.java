package xjtu.thinkerandperformer.memoryallocator.Controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Controller for Main.fxml
 */
public class MainController {
    private static final int blockWidth = 28;
    private static final int blockMargin = 2;
    private static final int padding = 5;
    private static final int blockSpace = blockWidth + blockMargin;

    private static final double outerRadius = 124d;
    private static final double innerRadius = 120d;
    private static final double scaleRatio = 3.0d;
    private static final double averageRadius = (innerRadius + outerRadius) / 2;


    @FXML
    public Label memoryInfoContainer;

    @FXML
    private Canvas buddyMemoryCanvas;


    private GraphicsContext ctx;

    private boolean isShowInspector = false;

    private void setShowInspector(boolean isShowInspector, double x, double y) {
        if (this.isShowInspector == isShowInspector) return;
        this.isShowInspector = isShowInspector;
        updateScene(x, y);
    }


    @FXML
    private void initialize() {
        System.out.println("Init Called");

        ctx = buddyMemoryCanvas.getGraphicsContext2D();

        System.out.println("Setup binding");
        buddyMemoryCanvas.setHeight(memoryInfoContainer.getHeight());
        buddyMemoryCanvas.setWidth(memoryInfoContainer.getHeight());
        buddyMemoryCanvas.widthProperty().addListener(observable -> updateScene());
        buddyMemoryCanvas.heightProperty().addListener(observable -> updateScene());
        buddyMemoryCanvas.heightProperty().bind(memoryInfoContainer.heightProperty());
        buddyMemoryCanvas.widthProperty().bind(memoryInfoContainer.widthProperty());

        buddyMemoryCanvas.setOnMouseEntered(e -> setShowInspector(true, e.getX(), e.getY()));
        buddyMemoryCanvas.setOnMouseExited(e -> setShowInspector(false, e.getX(), e.getY()));
        buddyMemoryCanvas.setOnMouseMoved(e -> updateScene(e.getX(), e.getY()));

        updateScene();
    }

    private void updateScene() {
        updateScene(-1, -1);
    }

    private void updateScene(double x, double y) {
        int width = (int) buddyMemoryCanvas.getWidth();
        int height = (int) buddyMemoryCanvas.getHeight();
        ctx.clearRect(0, 0, width, height);
        for (int h = padding; h < height - padding - blockWidth; h += blockWidth + blockMargin)
            for (int w = padding; w < width - padding - blockWidth; w += blockWidth + blockMargin) {
                double opacity = 0.2 + 0.6 * (0.5 * w / width + 0.5 * h / height);
                ctx.setFill(Color.web("#09c", opacity));
                ctx.fillRect(w, h, blockWidth, blockWidth);
            }

        if (isShowInspector && x >= 0 && y >= 0)
            drawInspector(x, y);
    }

    private void drawInspector(double centerX, double centerY) {

        int width = (int) buddyMemoryCanvas.getWidth();
        int height = (int) buddyMemoryCanvas.getHeight();


        // Save global context
        ctx.save();

        // View area of magnifier
        ctx.setFill(new Color(1, 1, 1, 1));
        ctx.fillOval(centerX - averageRadius, centerY - averageRadius, 2 * averageRadius, 2 * averageRadius);

        // Setup Clip
        ctx.save();
        ctx.beginPath();
        ctx.arc(centerX, centerY, averageRadius, averageRadius, 0, 2 * Math.PI * averageRadius);
        ctx.closePath();
        ctx.clip();

        // Fill content
        int blockIndexX = Math.max((int) ((centerX - averageRadius / scaleRatio - padding) / blockSpace), 0);
        int blockIndexY = Math.max((int) ((centerY - averageRadius / scaleRatio - padding) / blockSpace), 0);
        ctx.setFont(new Font(blockWidth * 0.4 * scaleRatio));
        for (int indexX = blockIndexX; indexX - blockIndexX < averageRadius * 2 / scaleRatio / blockSpace + 1; indexX++)
            for (int indexY = blockIndexY; indexY - blockIndexY < averageRadius * 2 / scaleRatio / blockSpace + 1; indexY++) {
                double opacity = 0.2 + 0.6 * (0.5 * (indexX * blockSpace) / width + 0.5 * (indexY * blockSpace) / height);
                ctx.setFill(Color.web("#09c", opacity));
                ctx.fillRect(
                        centerX - scaleRatio * (centerX - indexX * blockSpace - padding),
                        centerY - scaleRatio * (centerY - indexY * blockSpace - padding),
                        blockWidth * scaleRatio, blockWidth * scaleRatio);
                ctx.setFill(Color.web("#555", 0.8));
                ctx.fillText(
                        String.format("%2d", indexX + indexY * width / blockSpace),
                        centerX - scaleRatio * (centerX - (indexX * blockSpace + blockWidth * 0.1)),
                        centerY - scaleRatio * (centerY - (indexY * blockSpace + blockWidth * 0.7))
                );
            }

        // Remove Clip
        ctx.restore();

        // Draw outline
        ctx.setStroke(new Color(0.6, 0.4, 0.2, 1));
        ctx.setLineWidth(8);
        ctx.strokeOval(centerX - innerRadius, centerY - innerRadius, 2 * innerRadius, 2 * innerRadius);

        // Restore global context
        ctx.restore();
    }
}
