package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class IllegalParameterException extends MemoryManagerException {
    public IllegalParameterException() {
        super("参数错误");
    }

    public IllegalParameterException(String message) {
        super(message);
    }
}
