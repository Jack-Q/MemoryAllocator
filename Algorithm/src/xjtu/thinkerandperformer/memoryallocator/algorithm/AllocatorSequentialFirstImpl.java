package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;

public class AllocatorSequentialFirstImpl extends AllocatorSequential {

    public AllocatorSequentialFirstImpl(int size) throws NumberOutOfBoundsException {
        super(size);
        super.setSequentialFitMethod(new SequentialFirstFitMethod());
    }


    public static class SequentialFirstFitMethod implements AllocatorSequential.SequentialFitMethod {
        public int pickFreeBlock(int size, short[] memPool, MemHandle freelist) {
            System.out.println("(顺序适配方法——首先适配)");

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

}
