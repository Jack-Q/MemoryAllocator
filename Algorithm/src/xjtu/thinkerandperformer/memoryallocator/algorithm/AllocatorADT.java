package xjtu.thinkerandperformer.memoryallocator.algorithm;

interface AllocatorADT {

    /*初始化存储池*/
    void init(int size);

    /*为变量申请空间*/
    Variable newVariable(String variableName, int size);

    /*向变量存储空间写入数据*/
    boolean write(Variable variable, String value);

    /*从变量存储空间读数据*/
    String read(Variable variable);

    /*删除变量*/
    void deleteVariable(Variable variable);

    /*展示存储池*/
    void show();
}