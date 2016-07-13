package xjtu.thinkerandperformer.memoryallocator.algorithm;

class AllocatorSequentialFirstImpl extends AllocatorSequential {

    public AllocatorSequentialFirstImpl(int size) {
        super(size);
    }

    protected int pickFreeBlock(int size) {
        System.out.println("(测试信息)首先适配");

        if (freelist == null) return -1;  //无空闲块
        int freeStart = freelist.getPos();
        for (int curr = freeStart; ; )
            if (memPool[curr + FULL_SIZE] >= (size + MIN_EXTRA))
                return curr;
            else {
                curr = memPool[curr + R_PTR];
                if (curr == freeStart) return -1; // 已经查找完整个可利用空间表，未找到
            }
    }

}
