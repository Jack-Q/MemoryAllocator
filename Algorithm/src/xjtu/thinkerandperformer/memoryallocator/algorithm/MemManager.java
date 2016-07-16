package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MemManager {

    protected AllocatorADT allocator;
    protected Map<String, Variable> varMap;


    /*初始化存储池*/
    public void init(int size) throws NumberOutOfBoundsException {
        if (size <= 0) throw new NumberOutOfBoundsException();//抛出异常

        allocator = new AllocatorSequentialBestImpl(size);
        // allocator = new AllocatorBuddyImpl(size);
        varMap = new HashMap<>();
    }

    /*为变量申请空间*/
    public void newVariable(String variableName, int size) throws MemoryPoolUninitializedException, IllegalParameterException, InsufficientMemoryPoolException {
        if (size <= 0) throw new NumberOutOfBoundsException();//抛出异常
        if (allocator == null) throw new MemoryPoolUninitializedException();
        if (!variableName.matches("[A-Za-z_$][A-Za-z0-9_$]{0,6}")) throw new IllegalVariableNameException();
        if (varMap.containsKey(variableName)) throw new IllegalParameterException("变量已存在");

        Variable temp = allocator.newVariable(variableName, size);
        if (temp != null) varMap.put(variableName, temp);
    }

    /*向变量存储空间写入数据*/
    public void write(String variableName, String value) throws MemoryPoolUninitializedException, VariableNotFoundException, InsufficientVariableSizeException {
        if (allocator == null) throw new MemoryPoolUninitializedException();
        if (!varMap.containsKey(variableName)) throw new VariableNotFoundException();

        else allocator.write(varMap.get(variableName), value);
    }

    /*从变量存储空间读数据*/
    public void read(String variableName) throws VariableNotFoundException, MemoryPoolUninitializedException, VariableNotAssignedException {
        if (allocator == null) throw new MemoryPoolUninitializedException();
        if (!varMap.containsKey(variableName)) throw new VariableNotFoundException();

        else System.out.println(allocator.read(varMap.get(variableName)));
    }

    /*删除变量*/
    public void deleteVariable(String variableName) throws MemoryPoolUninitializedException, VariableNotFoundException {
        if (allocator == null) throw new MemoryPoolUninitializedException();
        if (!varMap.containsKey(variableName)) throw new VariableNotFoundException();

        else {
            allocator.deleteVariable(varMap.get(variableName));
            varMap.remove(variableName);
        }
    }

    /*展示存储池*/
    public void show() throws MemoryPoolUninitializedException {
        if (allocator == null) throw new MemoryPoolUninitializedException();

        List<String> sortVariableList = getSortedVariableList();
        allocator.show(allocator, sortVariableList);
    }

    protected List<String> getSortedVariableList() {
        return varMap.entrySet().stream().sorted((e1, e2) ->
                e1.getValue().getHandle().getPos() - e2.getValue().getHandle().getPos()
        ).map(Map.Entry::getKey).collect(Collectors.toList());
    }


}
