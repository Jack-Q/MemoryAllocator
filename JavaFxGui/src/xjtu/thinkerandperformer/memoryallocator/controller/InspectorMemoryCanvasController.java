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
import xjtu.thinkerandperformer.memoryallocator.algorithm.BitBlockInfo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * controller for InspectorMemoryCanvas.fxml
 */
public class InspectorMemoryCanvasController implements Initializable {
    private static final int blockWidth = 28;
    private static final int blockMargin = 2;
    private static final int margin = 5;
    private static final int blockSpace = blockWidth + blockMargin;

    private static final double outerRadius = 164d;
    private static final double innerRadius = 160d;
    private static final double maxScaleRatio = 10.0d;
    private static final double minScaleRatio = 1.0d;
    private double scaleRatio = 1.0d;
    private static final double averageRadius = (innerRadius + outerRadius) / 2;

    private int blockCount = 0;

    @FXML
    public Label label;

    @FXML
    private Canvas canvas;


    private GraphicsContext ctx;

    private boolean isShowInspector = false;
    private int blockColumnCount;
    private int blockRowCount;
    private double originalZoomFactor;
    private List<BitBlockInfo> blockInformationList;

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

        if (blockCount == 0) {
            Utility.showCenterMessage("No Memory Pool Initialized", width, height, ctx);
            return;
        }
        updateBlockCount();

        ctx.clearRect(0, 0, width, height);

        for (int i = 0; i < blockRowCount; i++)
            for (int j = 0; j < blockColumnCount; j++)
                if (i * blockColumnCount + j < blockCount) {
                    double h = margin + i * blockSpace * originalZoomFactor;
                    double w = margin + j * blockSpace * originalZoomFactor;
                    double opacity = 0.2 + 0.6 * (0.5 * w / width + 0.5 * h / height);
                    ctx.setFill(Color.web("#09c", opacity));
                    ctx.fillRect(w, h, blockWidth * originalZoomFactor, blockWidth * originalZoomFactor);
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

        // Fill content
        int blockIndexX = Math.max((int) ((centerX - averageRadius / scaleRatio - margin) / blockSpace / originalZoomFactor), 0);
        int blockIndexY = Math.max((int) ((centerY - averageRadius / scaleRatio - margin) / blockSpace / originalZoomFactor), 0);

        ctx.setFont(new Font(blockWidth * 0.4 * scaleRatio * originalZoomFactor));
        double maxIndexX = Math.min(blockIndexX + averageRadius * 2 / scaleRatio / blockSpace / originalZoomFactor + 1, blockColumnCount);
        double mavIndexY = Math.min(blockIndexY + averageRadius * 2 / scaleRatio / blockSpace / originalZoomFactor + 1, blockColumnCount);

        //
        // Implementation Note:
        // Canvas clip is a  extremely expensive action with involves in calculating/detecting each pixels draw by
        // sub-sequential actions whether is in the inner region of a path clip. In this signifier implementation,
        // the delay is significant when the number of memory blocks large enough.
        //

        // Setup Clip
        ctx.save();
        ctx.beginPath();
        ctx.arc(centerX, centerY, averageRadius, averageRadius, 0, 2 * Math.PI * averageRadius);
        ctx.closePath();
        ctx.clip();

        // Current policy is only draw the blocks on the boundary with the clip
        for (int indexX = blockIndexX; indexX < maxIndexX; indexX++) {
            for (int indexY = blockIndexY; indexY < mavIndexY; indexY++) {
                if (indexX + indexY * blockColumnCount >= blockCount) continue;
                double x = centerX - scaleRatio * (centerX - indexX * blockSpace * originalZoomFactor - margin);
                double y = centerY - scaleRatio * (centerY - indexY * blockSpace * originalZoomFactor - margin);

                // drop redundant dawn by detecting whether the block is off-site of the region
                if (Math.pow(centerX - x - blockSpace * scaleRatio * originalZoomFactor / 2, 2)
                        + Math.pow(centerY - y - blockSpace * scaleRatio * originalZoomFactor / 2, 2)
                        > Math.pow(averageRadius + blockSpace * scaleRatio * originalZoomFactor / 2 * 1.3/* error tolerance */, 2))
                    continue;

                // let the inside block to be rendered later
                if(Math.pow(centerX - x - blockSpace * scaleRatio * originalZoomFactor / 2, 2)
                        + Math.pow(centerY - y - blockSpace * scaleRatio * originalZoomFactor / 2, 2)
                        < Math.pow(averageRadius - blockSpace * scaleRatio * originalZoomFactor / 2 * 1.3/* error tolerance */, 2))
                    continue;

                double opacity = 0.2 + 0.6 * (0.5 * (indexX * blockSpace * originalZoomFactor) / width + 0.5 * (indexY * blockSpace * originalZoomFactor) / height);
                ctx.setFill(Color.web("#09c", opacity));
                ctx.fillRect(
                        x,
                        y,
                        blockWidth * scaleRatio * originalZoomFactor, blockWidth * scaleRatio * originalZoomFactor);
                ctx.setFill(Color.web("#555", 0.8));

                String id = Integer.toString(indexX + indexY * blockColumnCount);
                ctx.fillText(
                        id,
                        centerX - scaleRatio * (centerX - (indexX * blockSpace + blockWidth * (0.5 - 0.09 * id.length())) * originalZoomFactor - margin),
                        centerY - scaleRatio * (centerY - (indexY * blockSpace + blockWidth * 0.3) * originalZoomFactor - margin)
                );
            }
        }

        // Remove Clip
        ctx.restore();

        // Then draw the inside blocks without clip region which will significantly improve performance
        for (int indexX = blockIndexX; indexX < maxIndexX; indexX++) {
            for (int indexY = blockIndexY; indexY < mavIndexY; indexY++) {
                if (indexX + indexY * blockColumnCount >= blockCount) continue;
                double x = centerX - scaleRatio * (centerX - indexX * blockSpace * originalZoomFactor - margin);
                double y = centerY - scaleRatio * (centerY - indexY * blockSpace * originalZoomFactor - margin);

                // let the inside block to be rendered later
                if(Math.pow(centerX - x - blockSpace * scaleRatio * originalZoomFactor / 2, 2)
                        + Math.pow(centerY - y - blockSpace * scaleRatio * originalZoomFactor / 2, 2)
                        > Math.pow(averageRadius - blockSpace * scaleRatio * originalZoomFactor / 2 * 1.3/* error tolerance */, 2))
                    continue;

                double opacity = 0.2 + 0.6 * (0.5 * (indexX * blockSpace * originalZoomFactor) / width + 0.5 * (indexY * blockSpace * originalZoomFactor) / height);
                ctx.setFill(Color.web("#09c", opacity));
                ctx.fillRect(
                        x,
                        y,
                        blockWidth * scaleRatio * originalZoomFactor, blockWidth * scaleRatio * originalZoomFactor);
                ctx.setFill(Color.web("#555", 0.8));

                String id = Integer.toString(indexX + indexY * blockColumnCount);
                ctx.fillText(
                        id,
                        centerX - scaleRatio * (centerX - (indexX * blockSpace + blockWidth * (0.5 - 0.09 * id.length())) * originalZoomFactor - margin),
                        centerY - scaleRatio * (centerY - (indexY * blockSpace + blockWidth * 0.3) * originalZoomFactor - margin)
                );
            }
        }



        // Add translucent effect to magnifier
        ctx.setFill(new RadialGradient(0, 0, centerX, centerY, averageRadius, false, CycleMethod.NO_CYCLE,
                new Stop(0.4, Color.web("#fff", 0)), new Stop(0.8, Color.web("#fff", 0.2)), new Stop(1, Color.web("#fff", 0.8))));
        ctx.fillOval(centerX - averageRadius, centerY - averageRadius, 2 * averageRadius, 2 * averageRadius);

        // Draw outline
        ctx.setStroke(new Color(0.6, 0.4, 0.2, 1));
        ctx.setLineWidth(8);
        ctx.strokeOval(centerX - innerRadius, centerY - innerRadius, 2 * innerRadius, 2 * innerRadius);

        // Restore global context
        ctx.restore();
    }


    @SuppressWarnings("Duplicates")
    private void updateBlockCount() {
        final double width = canvas.getWidth() - 2 * margin;
        final double height = canvas.getHeight() - 2 * margin;

        double countPerPixel = Math.sqrt(blockCount / height / width);

        this.blockColumnCount = (int) Math.ceil(countPerPixel * width);
        this.blockRowCount = (int) Math.ceil(countPerPixel * height);

        originalZoomFactor = Math.min(
                width / this.blockColumnCount / blockSpace,
                height / this.blockRowCount / blockSpace
        );
    }

    /**
     * @param percent must be a double number between 0 and 1
     */
    public void setScaleFactor(double percent) {
        percent = Math.min(percent, 1.0d);
        percent = Math.max(percent, 0.0d);
        scaleRatio = minScaleRatio + (maxScaleRatio - minScaleRatio) * percent;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
        updateScene();
    }

    public void setBitBlockInformationList(List<BitBlockInfo> blockInformationList) {
        this.blockInformationList = blockInformationList;
    }
}
