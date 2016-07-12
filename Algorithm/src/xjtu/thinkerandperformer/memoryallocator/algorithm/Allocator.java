package xjtu.thinkerandperformer.memoryallocator.algorithm;

public interface Allocator {
    void init(int size);
    Variable newVariable(String variableName, int size);
    boolean write(Variable variable, String value);
    String read(Variable variable);
    void deleteVariable(Variable variable);

    void show();
}
