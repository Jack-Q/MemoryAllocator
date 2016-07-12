package xjtu.thinkerandperformer.memoryallocator.algorithm;

/**
 * Created by jackq on 7/6/16.
 */
public class AllocatorSequentialWorstImpl extends AllocatorSequentialFirstImpl implements Allocator {
    @Override
    int findNextFreeBlock(int blockSize) {
        int size = blockSize;
        int addr = MAGIC_POSITION_NONE;
        for (int pos = getMemoryFirstFree(); pos != MAGIC_POSITION_NONE; pos = getNextFreeBlock(pos))
            if (getBlockSize(pos) >= blockSize && getBlockSize(pos) > size) {
                size = getBlockSize(pos);
                addr = pos;
            }
        return addr;
    }
}
