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

    public String getValue() {
        switch (type) {
            case DataBlock:
                return Character.toString((char) value);
            case StartTagBlock:
                return "Start";
            case EndTagBlock:
                return "end";
            case UnusedDataBlock:
                return "";
            case UsedSizeBlock:
            case FullSizeBlock:
            case MetaSizeBlock:
                if (value < 0) return "null";
                break;
            case MetaPointerBlock:
            case PointerBlock:
                if (value <= 0) return "null";
                break;
            case MetaTagBlock:
                break;
            case FreeBlock:
                break;
        }
        return String.valueOf(value);
    }
}
