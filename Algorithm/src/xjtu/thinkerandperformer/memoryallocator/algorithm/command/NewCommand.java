package xjtu.thinkerandperformer.memoryallocator.algorithm.command;

import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.IllegalParameterException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.InsufficientMemoryPoolException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.MemoryPoolUninitializedException;


class NewCommand implements ICommand {
    private String varName;
    private int varSize;

    NewCommand(String varName, int varSize) {
        this.varName = varName;
        this.varSize = varSize;
    }

    @Override
    public void invoke(MemManager memManager) throws MemoryPoolUninitializedException, InsufficientMemoryPoolException, IllegalParameterException {
        memManager.newVariable(varName, varSize);
    }

    @Override
    public String getName() {
        return "new";
    }
}
