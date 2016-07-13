package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.*;

import java.util.HashMap;
import java.util.Map;

public class MemManager {

    private AllocatorADT allocator;
    private Map<String, Variable> varMap;

    /*初始化存储池*/
    void init(int size) throws NumberOutOfBoundsException {
        if (size <= 0) throw new NumberOutOfBoundsException();//抛出异常

        allocator = new AllocatorSequentialBestImpl(size);
        varMap = new HashMap<String, Variable>();
    }

    /*为变量申请空间*/
    void newVariable(String variableName, int size) throws MemoryPoolUninitializedException, NumberOutOfBoundsException, InsufficientMemoryPoolException {
        if (size <= 0) throw new NumberOutOfBoundsException();//抛出异常
        if (allocator == null) throw new MemoryPoolUninitializedException();
        //variableName合法性判断

        Variable temp = allocator.newVariable(variableName, size);
        if (temp != null) varMap.put(variableName, temp);
    }

    /*向变量存储空间写入数据*/
    void write(String variableName, String value) throws MemoryPoolUninitializedException, VariableNotFoundException, InsufficientVariableSizeException {
        if (allocator == null) throw new MemoryPoolUninitializedException();
        if (!varMap.containsKey(variableName)) throw new VariableNotFoundException();

        else allocator.write(varMap.get(variableName), value);
    }

    /*从变量存储空间读数据*/
    void read(String variableName) throws VariableNotFoundException, MemoryPoolUninitializedException, VariableNotAssignedException {
        if (allocator == null) throw new MemoryPoolUninitializedException();
        if (!varMap.containsKey(variableName)) throw new VariableNotFoundException();

        else System.out.println(allocator.read(varMap.get(variableName)));
    }

    /*删除变量*/
    void deleteVariable(String variableName) throws MemoryPoolUninitializedException, VariableNotFoundException {
        if (allocator == null) throw new MemoryPoolUninitializedException();
        if (!varMap.containsKey(variableName)) throw new VariableNotFoundException();

        else {
            allocator.deleteVariable(varMap.get(variableName));
            varMap.remove(variableName);
        }
    }

    /*展示存储池*/
    void show() throws MemoryPoolUninitializedException {
        if (allocator == null) throw new MemoryPoolUninitializedException();

        allocator.show();
    }
}
