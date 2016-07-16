package xjtu.thinkerandperformer.memoryallocator.algorithm.command;

import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.*;

public interface ICommand {
    void invoke(MemManager memManager) throws MemoryManagerException;

    String getName();
}



