package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class MemoryPoolUninitializedException extends NullObjectException {
    public MemoryPoolUninitializedException() {
        super("未初始化存储池");
    }

    public MemoryPoolUninitializedException(String message) {
        super(message);
    }
}
