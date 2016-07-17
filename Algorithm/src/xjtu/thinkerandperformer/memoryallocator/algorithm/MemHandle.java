package xjtu.thinkerandperformer.memoryallocator.algorithm;

public class MemHandle { // An access handle for the memory manager

    private int pos;               // Position of data in memory 数据在存储池中的位置

    MemHandle(int inPos) {  //带参构造方法
        pos = inPos;
    }

    public int getPos() {  //返回句柄值
        return pos;
    }
}
