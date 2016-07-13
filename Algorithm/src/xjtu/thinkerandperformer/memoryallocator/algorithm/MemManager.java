package xjtu.thinkerandperformer.memoryallocator.algorithm;

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
    void newVariable(String variableName, int size) {
        Variable temp = allocator.newVariable(variableName, size);
        if (temp != null) varMap.put(variableName, temp);
    }

    /*向变量存储空间写入数据*/
    void write(String variableName, String value) {
        if (varMap.containsKey(variableName)) allocator.write(varMap.get(variableName), value);
        else System.out.println("变量不存在");
    }

    /*从变量存储空间读数据*/
    void read(String variableName) {
        if (varMap.containsKey(variableName)) System.out.println(allocator.read(varMap.get(variableName)));
        else System.out.println("变量不存在");
    }

    /*删除变量*/
    void deleteVariable(String variableName) {
        if (varMap.containsKey(variableName)) {
            allocator.deleteVariable(varMap.get(variableName));
            varMap.remove(variableName);
        } else System.out.println("变量不存在");
    }

    /*展示存储池*/
    void show() {
        allocator.show();
    }
}
