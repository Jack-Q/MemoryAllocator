package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class NullObjectException extends MemoryManagerException {
    public NullObjectException() {
        super("操作对象不存在");
    }

    public NullObjectException(String message) {
        super(message);
    }
}

