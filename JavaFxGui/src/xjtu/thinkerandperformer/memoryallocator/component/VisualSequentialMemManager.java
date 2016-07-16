package xjtu.thinkerandperformer.memoryallocator.component;

import xjtu.thinkerandperformer.memoryallocator.algorithm.AllocatorSequentialBestImpl;
import xjtu.thinkerandperformer.memoryallocator.algorithm.BitBlockInfo;
import xjtu.thinkerandperformer.memoryallocator.algorithm.BlockInfo;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;

import java.util.List;


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

    @Override
    public List<BlockInfo> getBlockInfoList() {
        return sequentialBest.getBlockInfoList(getSortedVariableList());
    }

    @Override
    public int getBlockCount() {
        return this.sequentialBest.getBlockCount();
    }

    @Override
    public List<BitBlockInfo> getBitBlockInformationList() {
        return this.sequentialBest.getBitBlockInformationList();
    }
}


