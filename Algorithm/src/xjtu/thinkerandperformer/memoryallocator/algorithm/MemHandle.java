package xjtu.thinkerandperformer.memoryallocator.algorithm;

class MemHandle { // An access handle for the memory manager

    int pos;               // Position of data in memory 数据在存储池中的位置

    MemHandle(int inpos) {  //带参构造方法
        pos = inpos;
    }

    int getPos() {  //返回句柄值
        return pos;
    }
}
