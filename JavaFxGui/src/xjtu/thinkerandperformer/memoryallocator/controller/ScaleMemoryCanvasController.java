package xjtu.thinkerandperformer.memoryallocator.controller;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import xjtu.thinkerandperformer.memoryallocator.algorithm.BitBlockInfo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;


public class ScaleMemoryCanvasController implements Initializable {
    private Consumer<Double> scaleChangeListener = null;

    private static final double margin = 10;

    private static final double blockWidth = 28;
    private static final double blockSpace = 30;

    private int blockCount = 0;
    private int blockRowCount = 20;
    private int blockColumnCount = 16;
    private double originalZoomFactor = 0.0d;

    private static final double maxZoomFactor = 10.0d;
    private static final double minZoomFactor = 1.0d;
    private double zoomFactor = 1.0d;

    @FXML
    public Canvas canvas;

    @FXML
    public Label container;

    private GraphicsContext ctx;
    private double centerX = 0;
    private double centerY = 0;

    private boolean isDrag = false;
    private double mousePositionX = 0.0d;
    private double mousePositionY = 0.0d;
    private List<BitBlockInfo> blockInformationList;


    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Set graphic context
        ctx = canvas.getGraphicsContext2D();

        // Bind resize function
        this.canvas.widthProperty().addListener(e -> repaint());
        this.canvas.heightProperty().addListener(e -> repaint());
        this.canvas.widthProperty().bind(this.container.widthProperty());
        this.canvas.heightProperty().bind(this.container.heightProperty());

        // Bind event handler
        this.canvas.setOnDragDetected(e -> {
            isDrag = true;
            mousePositionX = e.getScreenX();
            mousePositionY = e.getScreenY();
        });
        this.canvas.setOnMouseDragged(e -> {
            if (!isDrag) {
                isDrag = true;
            } else {
                centerX -= (e.getScreenX() - mousePositionX) / zoomFactor;
                centerY -= (e.getScreenY() - mousePositionY) / zoomFactor;
                repaint();
            }
            mousePositionX = e.getScreenX();
            mousePositionY = e.getScreenY();

        });
        this.canvas.setOnMouseReleased(e -> {
            if (!isDrag) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    // Left Button (Primary Button)
                    zoomIn(e.getX(), e.getY());
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    // Right Button (Secondary Button)
                    zoomOut(e.getX(), e.getY());
                }
            }
            isDrag = false;
        });

        // initial scene
        updateBlockCount();
    }


    private void zoomIn(double zoomCenterX, double zoomCenterY) {
        if (zoomFactor < maxZoomFactor / originalZoomFactor) {
            zoom(zoomCenterX, zoomCenterY, Math.min(maxZoomFactor / originalZoomFactor, zoomFactor * 2));
        }
    }

    private void zoomOut(double zoomCenterX, double zoomCenterY) {
        if (zoomFactor > minZoomFactor) {
            zoom(zoomCenterX, zoomCenterY, Math.max(minZoomFactor, zoomFactor * 0.5));
        }
    }


    private double delta;
    private double original;
    private double zoomCenterX;
    private double zoomCenterY;

    private void zoom(double cx, double cy, double newScale) {
        this.delta = newScale - zoomFactor;
        this.original = zoomFactor;
        this.zoomCenterX = (cx - canvas.getWidth() / 2) / original + centerX;
        this.zoomCenterY = (cy - canvas.getHeight() / 2) / original + centerY;
        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(400));
                setInterpolator(Interpolator.EASE_BOTH);
                setOnFinished(e -> {
                    zoomFactor = original + delta;
                    centerX = ensureCenterX(zoomCenterX - (zoomCenterX - centerX) / zoomFactor * original, zoomFactor);
                    centerY = ensureCenterY(zoomCenterY - (zoomCenterY - centerY) / zoomFactor * original, zoomFactor);
                    repaint();
                    notifyStateChange();
                });
            }

            @Override
            protected void interpolate(double fraction) {
                double scale = original + fraction * delta;

                repaint(
                        ensureCenterX(zoomCenterX - (zoomCenterX - centerX) / scale * original, scale),
                        ensureCenterY(zoomCenterY - (zoomCenterY - centerY) / scale * original, scale),
                        scale);

            }
        };
        transition.play();
    }

    private void notifyStateChange() {
        if (scaleChangeListener != null)
            scaleChangeListener.accept((this.zoomFactor - minZoomFactor) / (maxZoomFactor / originalZoomFactor - minZoomFactor));
    }

    private void repaint() {
        repaint(centerX = ensureCenterX(centerX, zoomFactor), centerY = ensureCenterY(centerY, zoomFactor), zoomFactor);
    }

    private void repaint(double centerX, double centerY, double scale) {
        updateBlockCount();

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (blockCount == 0) {
            Utility.showCenterMessage("No Memory Pool Initialized", width, height, ctx);
            return;
        }

        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double actualBlockWidth = blockWidth * originalZoomFactor * scale;
        double actualBlockSpace = blockSpace * originalZoomFactor * scale;

        int indexStartX = Math.max(0, (int) ((centerX - margin) / blockSpace / originalZoomFactor - width / 2 / actualBlockSpace) - 2);
        int indexStartY = Math.max(0, (int) ((centerY - margin) / blockSpace / originalZoomFactor - height / 2 / actualBlockSpace) - 2);

        int actualBlockRowCount = (int) (height / actualBlockSpace + 4);
        int actualBLockColumnCount = (int) (width / actualBlockSpace + 4);

        for (int i = indexStartY;
             i < Math.min(this.blockRowCount, indexStartY + actualBlockRowCount); i++)
            for (int j = indexStartX;
                 j < Math.min(this.blockColumnCount, indexStartX + actualBLockColumnCount); j++) {
                int index = i * blockColumnCount + j;
                if (index >= blockCount) break;
                Utility.drawBitBlock(
                        ctx,
                        (margin + j * blockSpace * originalZoomFactor - centerX) * scale + width / 2,
                        (margin + i * blockSpace * originalZoomFactor - centerY) * scale + height / 2,
                        index,
                        blockInformationList.get(index),
                        actualBlockWidth
                );
            }
    }

    @SuppressWarnings("Duplicates")
    private void updateBlockCount() {
        double width = canvas.getWidth() - 2 * margin;
        double height = canvas.getHeight() - 2 * margin;

        double countPerPixel = Math.sqrt(blockCount / width / height);

        this.blockColumnCount = (int) Math.ceil(countPerPixel * width);
        this.blockRowCount = (int) Math.ceil(countPerPixel * height);

        originalZoomFactor = Math.min(
                width / this.blockColumnCount / blockSpace,
                height / this.blockRowCount / blockSpace
        );
    }

    private double ensureCenterX(double centerX, double zoomFactor) {
        return Math.min(Math.max(centerX, canvas.getWidth() / 2 / zoomFactor), canvas.getWidth() - canvas.getWidth() / 2 / zoomFactor);
    }

    private double ensureCenterY(double centerY, double zoomFactor) {
        return Math.min(Math.max(centerY, canvas.getHeight() / 2 / zoomFactor), canvas.getHeight() - canvas.getHeight() / 2 / zoomFactor);
    }


    // region memory information

    /**
     * @param percent must be a double number between 0 and 1
     */
    void setScaleFactor(double percent) {
        percent = Math.min(percent, 1.0d);
        percent = Math.max(percent, 0.0d);
        this.zoomFactor = minZoomFactor + (maxZoomFactor / originalZoomFactor - minZoomFactor) * percent;
        repaint();
    }

    void setScaleChangeListener(Consumer<Double> c) {
        this.scaleChangeListener = c;
    }


    void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
        repaint();
    }

    void setBitBlockInformationList(List<BitBlockInfo> blockInformationList) {
        this.blockInformationList = blockInformationList;
        repaint();
    }
    // endregion
}
