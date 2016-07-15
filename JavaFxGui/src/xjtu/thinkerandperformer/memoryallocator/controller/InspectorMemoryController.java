package xjtu.thinkerandperformer.memoryallocator.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * controller for InspectorMemoryCanvas.fxml
 */
public class InspectorMemoryController implements Initializable {
    private static final int blockWidth = 28;
    private static final int blockMargin = 2;
    private static final int padding = 5;
    private static final int blockSpace = blockWidth + blockMargin;

    private static final double outerRadius = 124d;
    private static final double innerRadius = 120d;
    private static final double scaleRatio = 2.0d;
    private static final double averageRadius = (innerRadius + outerRadius) / 2;


    @FXML
    public Label label;

    @FXML
    private Canvas canvas;


    private GraphicsContext ctx;

    private boolean isShowInspector = false;

    private void setShowInspector(boolean isShowInspector, double x, double y) {
        if (this.isShowInspector == isShowInspector) return;
        this.isShowInspector = isShowInspector;
        updateScene(x, y);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init Called");

        ctx = canvas.getGraphicsContext2D();

        System.out.println("Setup binding");
        canvas.setHeight(label.getHeight());
        canvas.setWidth(label.getHeight());
        canvas.widthProperty().addListener(observable -> updateScene());
        canvas.heightProperty().addListener(observable -> updateScene());
        canvas.heightProperty().bind(label.heightProperty());
        canvas.widthProperty().bind(label.widthProperty());

        canvas.setOnMouseEntered(e -> setShowInspector(true, e.getX(), e.getY()));
        canvas.setOnMouseExited(e -> setShowInspector(false, e.getX(), e.getY()));
        canvas.setOnMouseMoved(e -> updateScene(e.getX(), e.getY()));

        updateScene();
    }

    private void updateScene() {
        updateScene(-1, -1);
    }

    private void updateScene(double x, double y) {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        ctx.clearRect(0, 0, width, height);
        for (int h = padding; h < height - padding - blockWidth; h += blockSpace)
            for (int w = padding; w < width - padding - blockWidth; w += blockSpace) {
                double opacity = 0.2 + 0.6 * (0.5 * w / width + 0.5 * h / height);
                ctx.setFill(Color.web("#09c", opacity));
                ctx.fillRect(w, h, blockWidth, blockWidth);
            }

        if (isShowInspector && x >= 0 && y >= 0)
            drawInspector(x, y);
    }

    private void drawInspector(double centerX, double centerY) {

        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();


        // Save global context
        ctx.save();

        // view area of magnifier
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
        for (int indexX = blockIndexX;
             indexX - blockIndexX < averageRadius * 2 / scaleRatio / blockSpace + 1 && padding + indexX * blockSpace < width - padding - blockWidth;
             indexX++)
            for (int indexY = blockIndexY;
                 indexY - blockIndexY < averageRadius * 2 / scaleRatio / blockSpace + 1 && padding + indexY * blockSpace < height - padding - blockWidth;
                 indexY++) {
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


        // Add translucent effect to magnifier
        ctx.setFill(new RadialGradient(0, 0, centerX, centerY, averageRadius, false, CycleMethod.NO_CYCLE,
                new Stop(0.4, Color.web("#fff", 0)),new Stop(0.8, Color.web("#fff", 0.2)),  new Stop(1, Color.web("#fff", 0.8))));
        ctx.fillOval(centerX - averageRadius, centerY - averageRadius, 2 * averageRadius, 2 * averageRadius);

        // Draw outline
        ctx.setStroke(new Color(0.6, 0.4, 0.2, 1));
        ctx.setLineWidth(8);
        ctx.strokeOval(centerX - innerRadius, centerY - innerRadius, 2 * innerRadius, 2 * innerRadius);

        // Restore global context
        ctx.restore();
    }

}
