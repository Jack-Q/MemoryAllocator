package xjtu.thinkerandperformer.memoryallocator.algorithm.command;

import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;

public class InitCommand implements ICommand {
    final int memPoolSize;

    public InitCommand(int memPoolSize) {
        this.memPoolSize = memPoolSize;
    }

    @Override
    public void invoke(MemManager memManager) throws NumberOutOfBoundsException {
        memManager.init(memPoolSize);
    }

    @Override
    public String getName() {
        return "init";
    }
}
