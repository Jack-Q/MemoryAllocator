package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class MemoryManagerException extends Exception {
    public MemoryManagerException() {
        this("错误");
    }

    public MemoryManagerException(String message) {
        super(message);
    }
}
