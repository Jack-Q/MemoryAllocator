package xjtu.thinkerandperformer.memoryallocator.algorithm;

public class Variable {

    private MemHandle handle;

    /*构造方法*/
    public Variable(MemHandle handle) {
        this.handle = handle;
    }

    /*获得句柄*/
    public MemHandle getHandle() {
        return handle;
    }

    /*设置句柄*/
    public void setHandle(MemHandle handleValue) {
        handle = handleValue;
    }
}
