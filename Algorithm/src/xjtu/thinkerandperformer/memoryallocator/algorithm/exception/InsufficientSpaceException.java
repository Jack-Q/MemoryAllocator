package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class InsufficientSpaceException extends MemoryManagerException {
    public InsufficientSpaceException() {
        super("空间不足");
    }

    public InsufficientSpaceException
            (String message) {
        super(message);
    }
}
