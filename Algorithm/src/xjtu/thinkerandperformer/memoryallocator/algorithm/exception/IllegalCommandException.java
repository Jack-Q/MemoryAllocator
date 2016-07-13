package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class IllegalCommandException extends IllegalParameterException {
    public IllegalCommandException() {
        super("无法识别的命令");
    }

    public IllegalCommandException(String message) {
        super(message);
    }
}
