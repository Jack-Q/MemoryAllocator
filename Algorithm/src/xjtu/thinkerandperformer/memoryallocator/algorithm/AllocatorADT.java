package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.InsufficientMemoryPoolException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.InsufficientVariableSizeException;
import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.VariableNotAssignedException;

import java.util.List;

interface AllocatorADT {

    /*初始化存储池*/
    void init(int size);

    /*为变量申请空间*/
    Variable newVariable(String variableName, int size) throws InsufficientMemoryPoolException;

    /*向变量存储空间写入数据*/
    boolean write(Variable variable, String value) throws InsufficientVariableSizeException;

    /*从变量存储空间读数据*/
    String read(Variable variable) throws VariableNotAssignedException;

    /*删除变量*/
    void deleteVariable(Variable variable);

    /*展示存储池*/
    void show(AllocatorADT allocator, List<String> sortVariableList);
}