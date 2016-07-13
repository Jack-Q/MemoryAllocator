package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;


public class VariableNotAssignedException extends NullObjectException {
    public VariableNotAssignedException() {
        super("变量未赋值");
    }

    public VariableNotAssignedException(String message) {
        super(message);
    }
}
