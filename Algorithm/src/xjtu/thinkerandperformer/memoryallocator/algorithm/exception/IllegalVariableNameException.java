package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;

public class IllegalVariableNameException extends IllegalParameterException {
    public IllegalVariableNameException() {
        super("变量名不合规范");
    }

    public IllegalVariableNameException(String message) {
        super(message);
    }
}
