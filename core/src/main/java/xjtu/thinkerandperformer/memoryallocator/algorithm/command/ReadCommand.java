package xjtu.thinkerandperformer.memoryallocator.algorithm.command;

import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.MemoryPoolUninitializedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotAssignedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotFoundException;


class ReadCommand implements ICommand {
    private final String varName;

    ReadCommand(String varName) {
        this.varName = varName;
    }


    @Override
    public void invoke(MemManager memManager) throws VariableNotFoundException, VariableNotAssignedException, MemoryPoolUninitializedException {
        memManager.read(varName);
    }

    @Override
    public String getName() {
        return "read";
    }
}
