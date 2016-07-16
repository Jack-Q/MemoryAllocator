package xjtu.thinkerandperformer.memoryallocator.algorithm;

/**
 * Created by jackq on 7/17/16.
 */
public class BitBlockInfo {
    private final MemoryBlockType type;
    private final int value;

    public BitBlockInfo(MemoryBlockType type, int value) {
        this.type = type;
        this.value = value;
    }

    public MemoryBlockType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}
