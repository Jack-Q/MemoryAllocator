package xjtu.thinkerandperformer.memoryallocator.algorithm;


public enum MemoryBlockType {
    MetaBlock,
    DataBlock,
    FullSizeBlock,
    UsedSizeBlock,
    PointerBlock,
    StartTagBlock,
    FreeBlock, UnusedDataBlock, EndTagBlock
}
