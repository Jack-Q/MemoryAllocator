package xjtu.thinkerandperformer.memoryallocator.algorithm;


public enum MemoryBlockType {
    MetaSizeBlock,
    MetaLinkBlock,
    MetaTagBlock,
    DataBlock,
    FullSizeBlock,
    UsedSizeBlock,
    PointerBlock,
    StartTagBlock,
    FreeBlock, UnusedDataBlock, EndTagBlock
}
