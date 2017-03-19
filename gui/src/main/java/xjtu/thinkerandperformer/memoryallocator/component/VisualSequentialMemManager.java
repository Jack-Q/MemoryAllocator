package xjtu.thinkerandperformer.memoryallocator.component;

import xjtu.thinkerandperformer.memoryallocator.algorithm.*;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;

import java.util.List;


public class VisualSequentialMemManager extends VisualMemManager {
    private AllocatorSequential allocatorSequential;
    private AllocatorSequential.SequentialFitMethod sequentialFitMethod;


    @Override
    public void init(int size) throws NumberOutOfBoundsException {
        super.init(size);
        allocator = allocatorSequential = new AllocatorSequential(size);
        allocatorSequential.setSequentialFitMethod(sequentialFitMethod);
    }

    @Override
    public int getBlockSize(int pos) {
        return allocatorSequential.getBlockSize(pos);
    }

    @Override
    public List<BlockInfo> getBlockInfoList() {
        return allocatorSequential.getBlockInfoList(getSortedVariableList());
    }

    @Override
    public int getBlockCount() {
        return this.allocatorSequential.getBlockCount();
    }

    @Override
    public List<BitBlockInfo> getBitBlockInformationList() {
        return this.allocatorSequential.getBitBlockInformationList();
    }


    public void setFitMethod(String methodName) {
        switch (methodName.toLowerCase()) {
            case "best":
                this.sequentialFitMethod = new AllocatorSequentialBestImpl.SequentialBestFitMethod();
                break;
            case "worst":
                this.sequentialFitMethod = new AllocatorSequentialWorstImpl.SequentialWorstFitMethod();
                break;
            case "first":
                this.sequentialFitMethod = new AllocatorSequentialFirstImpl.SequentialFirstFitMethod();
                break;
        }
        if (allocatorSequential != null)
            allocatorSequential.setSequentialFitMethod(this.sequentialFitMethod);
    }
}


