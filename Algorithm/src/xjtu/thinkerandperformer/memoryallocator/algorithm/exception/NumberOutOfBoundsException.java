package xjtu.thinkerandperformer.memoryallocator.algorithm.exception;


public class NumberOutOfBoundsException extends IllegalParameterException {
    public NumberOutOfBoundsException() {
        super("数字超范围");
    }

    public NumberOutOfBoundsException(String message) {
        super(message);
    }
}
