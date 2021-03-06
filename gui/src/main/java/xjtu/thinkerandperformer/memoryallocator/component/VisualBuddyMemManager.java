package xjtu.thinkerandperformer.memoryallocator.component;

import xjtu.thinkerandperformer.memoryallocator.algorithm.AllocatorBuddyImpl;
import xjtu.thinkerandperformer.memoryallocator.algorithm.BitBlockInfo;
import xjtu.thinkerandperformer.memoryallocator.algorithm.BlockInfo;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;

import java.util.List;

public class VisualBuddyMemManager extends VisualMemManager {
    private AllocatorBuddyImpl allocatorBuddy;

    @Override
    public void init(int size) throws NumberOutOfBoundsException {
        super.init(size);
        allocator = allocatorBuddy = new AllocatorBuddyImpl(size);
    }

    @Override
    public int getBlockSize(int pos) {
        return allocatorBuddy.getBLockSize(pos);
    }

    @Override
    public List<BlockInfo> getBlockInfoList() {
        return allocatorBuddy.getBlockInfoList(getSortedVariableList());
    }

    @Override
    public int getBlockCount() {
        return allocatorBuddy.getBlockCount();
    }

    @Override
    public List<BitBlockInfo> getBitBlockInformationList() {
        return allocatorBuddy.getBitBlockInformationList();
    }
}
