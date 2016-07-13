package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;


public class InsufficientMemoryPoolException extends InsufficientSpaceException {
    public InsufficientMemoryPoolException() {
        super("存储池空间不足");
    }

    public InsufficientMemoryPoolException(String message) {
        super(message);
    }
}
