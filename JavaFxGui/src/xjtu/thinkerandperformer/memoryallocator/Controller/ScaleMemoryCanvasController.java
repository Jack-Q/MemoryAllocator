package xjtu.thinkerandperformer.memoryallocator.Controller;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.lang.reflect.Method;

public class ScaleMemoryCanvasController {
    private static final double blockWidth = 28;
    private static final double blockMargin = 2;
    private static final double blockSpace = 30;

    private int blockCount = 320;

    private static final double maxZoomFactor = 10.0d;
    private static final double minZoomFactor = 1.0d;
    private static final double detailZoomFactor = 4;
    private double zoomFactor = 1.0d;

    @FXML
    public Canvas canvas;

    @FXML
    public Label container;

    private GraphicsContext ctx;
    private double centerX = 0;
    private double centerY = 0;


    @FXML
    public void initialize() {

        // Set graphic context
        ctx = canvas.getGraphicsContext2D();

        // Bind resize function
        this.canvas.setWidth(this.container.getWidth());
        this.canvas.setHeight(this.container.getHeight());
        this.canvas.widthProperty().addListener(e -> repaint());
        this.canvas.heightProperty().addListener(e -> repaint());
        this.canvas.widthProperty().bind(this.container.widthProperty());
        this.canvas.heightProperty().bind(this.container.heightProperty());

        // Bind event handler
        this.canvas.setOnMouseClicked(e -> {
            System.out.println("Clicked");
            if (e.getButton() == MouseButton.PRIMARY) {
                // Left Button (Primary Button)
                System.out.println("Zoom in");
                zoomIn(e.getSceneX(), e.getSceneY());
            } else if (e.getButton() == MouseButton.SECONDARY) {
                // Right Button (Secondary Button)
                System.out.println("Zoom out");
                zoomOut(e.getSceneX(), e.getSceneY());
            }
        });

        // initial scene
        repaint();
    }

    private void setCenter() {

    }

    private boolean zoomIn(double zoomCenterX, double zoomCenterY) {
        if (zoomFactor < maxZoomFactor) {
            zoom(zoomCenterX, zoomCenterY, Math.min(maxZoomFactor, zoomFactor * 1.5));
            return true;
        }
        return false;
    }

    private boolean zoomOut(double zoomCenterX, double zoomCenterY) {
        if (zoomFactor > minZoomFactor) {
            zoom(zoomCenterX, zoomCenterY, Math.max(minZoomFactor, zoomFactor * 0.6));
            return true;
        }
        return false;
    }


    private double delta;
    private double original;
    private double zoomCenterX;
    private double zoomCenterY;

    private void zoom(double zoomCenterX, double zoomCenterY, double newScale) {
        this.zoomCenterX = zoomCenterX;
        this.zoomCenterY = zoomCenterY;
        delta = newScale - zoomFactor;
        original = zoomFactor;
        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(500));
                setInterpolator(Interpolator.EASE_OUT);
            }
            @Override
            protected void interpolate(double frac) {
                zoomFactor = original + frac * delta;
                repaint(centerX, centerY, zoomFactor);
                System.out.println(frac);
            }
        };
        transition.play();
    }

    private void repaint() {
        repaint(centerX, centerY, zoomFactor);
    }

    private void repaint(double centerX, double centerY, double scale) {
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        boolean showDetail = scale > detailZoomFactor;
        ctx.setFill(Color.web("#000", scale / maxZoomFactor));
        ctx.fill();
        ctx.fillRect(0, 0, canvas.getWidth() / 2, canvas.getHeight()/ 2) ;
    }
}
