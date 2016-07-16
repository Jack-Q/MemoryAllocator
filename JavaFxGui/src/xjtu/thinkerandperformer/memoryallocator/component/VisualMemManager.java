package xjtu.thinkerandperformer.memoryallocator.component;

import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.Variable;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.MemoryPoolUninitializedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotAssignedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class VisualMemManager extends MemManager {

    private Consumer<String> readValueListener = null;

    public void setReadValueListener(Consumer<String> readValueListener) {
        this.readValueListener = readValueListener;
    }

    @Override
    public void init(int size) throws NumberOutOfBoundsException {
        if (size <= 0) throw new NumberOutOfBoundsException(); //throw new exception
        varMap = new HashMap<>();

    }

    @Override
    public void read(String variableName) throws VariableNotFoundException, MemoryPoolUninitializedException, VariableNotAssignedException {
        if (allocator == null) throw new MemoryPoolUninitializedException();
        if (!varMap.containsKey(variableName)) throw new VariableNotFoundException();
        else if (readValueListener != null)
            readValueListener.accept(allocator.read(varMap.get(variableName)));
    }

    public Map<String, Variable> getVariableMap() {
        return this.varMap;
    }

    public abstract int getBlockSize(int pos);
}
