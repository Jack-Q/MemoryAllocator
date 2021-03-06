package xjtu.thinkerandperformer.memoryallocator.component;

import xjtu.thinkerandperformer.memoryallocator.algorithm.BitBlockInfo;
import xjtu.thinkerandperformer.memoryallocator.algorithm.BlockInfo;
import xjtu.thinkerandperformer.memoryallocator.algorithm.MemManager;
import xjtu.thinkerandperformer.memoryallocator.algorithm.Variable;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.MemoryPoolUninitializedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotAssignedException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    public abstract List<BlockInfo> getBlockInfoList();

    public abstract int getBlockCount();

    public abstract List<BitBlockInfo> getBitBlockInformationList();

    public List<String> getVariableList() {
        // If no memory pool initialized, return an empty variable list
        if (varMap == null) return new ArrayList<>();

        return new ArrayList<>(varMap.keySet());
    }
}
