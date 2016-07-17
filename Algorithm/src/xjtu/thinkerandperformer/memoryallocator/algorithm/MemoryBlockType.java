package xjtu.thinkerandperformer.memoryallocator.algorithm;


import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum MemoryBlockType {
    MetaSizeBlock(Color.web("#0030cc", 0.3)),
    MetaPointerBlock(Color.web("#2060ba", 0.3)),
    MetaTagBlock(Color.web("#abda30", 0.3)),
    DataBlock(Color.web("#505662", 0.7)),
    FullSizeBlock(Color.web("#dad975", 0.7)),
    UsedSizeBlock(Color.web("#dad975", 0.8)),

    PointerBlock(Color.web("#2060ba", 0.6)),
    StartTagBlock(Color.web("#abda30", 0.5)),

    FreeBlock(Color.web("#88ffb0", 0.8)),
    UnusedDataBlock(Color.web("#44dd80", 0.8)),

    EndTagBlock(Color.web("#daab30", 0.5));

    private final Paint color;

    MemoryBlockType(Paint color) {
        this.color = color;
    }

    public Paint getColor() {
        return color;
    }

}
