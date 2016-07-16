package xjtu.thinkerandperformer.memoryallocator.algorithm.command;

import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.MemoryPoolUninitializedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotAssignedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotFoundException;

/**
 * Created by jackq on 7/16/16.
 */
public  class ReadCommand implements ICommand {
    String varName;

    public ReadCommand(String varName) {
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
