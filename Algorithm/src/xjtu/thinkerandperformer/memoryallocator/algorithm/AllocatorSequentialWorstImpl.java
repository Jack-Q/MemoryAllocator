package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;

public class AllocatorSequentialWorstImpl extends AllocatorSequential{

    public AllocatorSequentialWorstImpl(int size) throws NumberOutOfBoundsException {
        super(size);
        super.setSequentialFitMethod(new SequentialWorstFitMethod());
    }

    public static class SequentialWorstFitMethod implements AllocatorSequential.SequentialFitMethod {

        public int pickFreeBlock(int size, short[] memPool, MemHandle freelist) {
            System.out.println("(测试信息)最差适配");

            if (freelist == null) return -1;  //无空闲块
            int freeStart = freelist.getPos();

            int worstPos = freeStart;
            int sizeOfWorstPos = MIN_SHORT_INT;

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
}
