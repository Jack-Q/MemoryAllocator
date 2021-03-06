package xjtu.thinkerandperformer.memoryallocator.controller;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import xjtu.thinkerandperformer.memoryallocator.algorithm.BitBlockInfo;


class Utility {
    private static final Text measureTextView = new Text();

    // Keep a reference to this hidden scene in case it would be freed during GC
    @SuppressWarnings("unused")
    private static Scene measureScene = new Scene(new Group(measureTextView));

    private static Bounds measureFont(String text, Font font) {
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

    static void drawDot(GraphicsContext ctx, double x, double y) {
        drawDot(ctx, x, y, Color.RED);
    }

    static void drawDot(GraphicsContext ctx, double x, double y, Paint paint) {
        drawDot(ctx, x, y, 8, paint);
    }

    static void drawDot(GraphicsContext ctx, double x, double y, double size, Paint paint) {
        ctx.save();
        ctx.setFill(paint);
        ctx.fillRoundRect(x - size / 2, y - size / 2, size, size, size, size);
        ctx.restore();
    }
}
