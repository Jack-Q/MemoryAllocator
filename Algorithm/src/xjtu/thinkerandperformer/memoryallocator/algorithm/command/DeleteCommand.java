package xjtu.thinkerandperformer.memoryallocator.algorithm.command;

import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.MemoryPoolUninitializedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotFoundException;

public  class DeleteCommand implements ICommand {
    final String varName;

    public DeleteCommand(String varName) {
        this.varName = varName;
    }

    @Override
    public void invoke(MemManager memManager) throws MemoryPoolUninitializedException, VariableNotFoundException {
        memManager.deleteVariable(varName);
    }

    @Override
    public String getName() {
        return "delete";
    }
}
