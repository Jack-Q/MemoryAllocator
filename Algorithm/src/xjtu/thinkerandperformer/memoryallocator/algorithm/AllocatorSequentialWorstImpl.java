package xjtu.thinkerandperformer.memoryallocator.algorithm;

class AllocatorSequentialWorstImpl extends AllocatorSequential {

    public AllocatorSequentialWorstImpl(int size) {
        super(size);
    }

    protected int pickFreeBlock(int size) {
        System.out.println("(测试信息)最差适配");

        if (freelist == null) return -1;  //无空闲块
        int freeStart = freelist.getPos();

        int worstPos = freeStart;
        int sizeOfWorstPos = MIN_SHORTINT;

        int curr = freeStart;
        do {
            if (memPool[curr + FULL_SIZE] > sizeOfWorstPos) {
                worstPos = curr;
                sizeOfWorstPos = memPool[worstPos + FULL_SIZE];
            }
            curr = memPool[curr + R_PTR];
        }
        while (curr != freeStart);

        if (memPool[worstPos + FULL_SIZE] >= (size + MIN_EXTRA)) return worstPos;
        else return -1;
    }
}
