package xjtu.thinkerandperformer.memoryallocator.controller;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import xjtu.thinkerandperformer.memoryallocator.algorithm.BitBlockInfo;

/**
 * Created by jackq on 7/16/16.
 */
public class Utility {
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

    static void drawBitBlock(GraphicsContext ctx, double positionX, double positionY, int index, BitBlockInfo bitBlockInfo, double size) {
        ctx.setFill(bitBlockInfo.getType().getColor());
        ctx.fillRect(
                positionX,
                positionY,
                size, size);

        if (size < 30) return;
        ctx.setFont(Font.font(size / 3));
        ctx.setFill(Color.web("#39c", 0.8));
        String contentValue = bitBlockInfo.getValue();
        ctx.fillText(
                contentValue,
                positionX + size * (0.5 - 0.1 * contentValue.length()),
                positionY + size / 2 - 3);

        ctx.setFill(Color.web("#555", 0.4));
        String indexString = String.valueOf(index);
        ctx.fillText(
                indexString,
                positionX + size * (0.5 - 0.1 * indexString.length()),
                positionY + size - 3);

    }
}
