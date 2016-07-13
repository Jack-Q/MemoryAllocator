package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class InsufficientVariableSizeException extends InsufficientSpaceException {
    public InsufficientVariableSizeException() {
        super("变量空间不足");
    }

    public InsufficientVariableSizeException(String message) {
        super(message);
    }
}
