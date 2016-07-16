package xjtu.thinkerandperformer.memoryallocator.controller;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Created by jackq on 7/16/16.
 */
public  class Utility {
    private static Text measureTextView = new Text();
    private static Scene measureScene =
            new Scene(new Group(measureTextView));

    static Bounds measureFont(String text, Font font) {
        measureTextView.setText(text);
        measureTextView.setFont(font);
        measureTextView.applyCss();
        return measureTextView.getLayoutBounds();
    }

    static void showCenterMessage(String message, double width, double height, GraphicsContext ctx) {
        ctx.clearRect(0, 0, width, height);
        Font font = new Font("Monaco", 30);
        Bounds bounds = measureFont(message, font);
        double fontWidth = bounds.getWidth();
        double fontHeight = bounds.getHeight();
        ctx.setFill(Color.web("#556677"));
        ctx.setFont(font);
        ctx.fillText(message, (width - fontWidth) / 2, (height - fontHeight) / 2);
    }
}
