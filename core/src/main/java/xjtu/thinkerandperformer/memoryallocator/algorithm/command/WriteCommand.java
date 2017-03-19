package xjtu.thinkerandperformer.memoryallocator.algorithm.command;

import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.InsufficientVariableSizeException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.MemoryPoolUninitializedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotFoundException;


public class WriteCommand implements ICommand {
    final String varName;
    final String value;

    public WriteCommand(String varName, String value) {
        this.value = value;
        this.varName = varName;
    }


    @Override
    public void invoke(MemManager memManager) throws MemoryPoolUninitializedException, InsufficientVariableSizeException, VariableNotFoundException {
        memManager.write(varName, value);
    }

    @Override
    public String getName() {
        return "write";
    }
}
