package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class VariableNotFoundException extends NullObjectException {
    public VariableNotFoundException() {
        super("变量不存在");
    }

    public VariableNotFoundException(String message) {
        super(message);
    }
}
