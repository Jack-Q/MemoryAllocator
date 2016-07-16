package xjtu.thinkerandperformer.memoryallocator.component;

import xjtu.thinkerandperformer.memoryallocator.algorithm.AllocatorSequentialBestImpl;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;


public class VisualSequentialMemManager extends VisualMemManager {
    AllocatorSequentialBestImpl sequentialBest;

    @Override
    public void init(int size) throws NumberOutOfBoundsException {
        super.init(size);
        allocator = sequentialBest = new AllocatorSequentialBestImpl(size);
    }

    @Override
    public int getBlockSize(int pos) {
        return sequentialBest.getBlockSize(pos);
    }
}


