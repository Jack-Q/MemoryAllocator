package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.NumberOutOfBoundsException;


public class AllocatorSequentialBestImpl extends AllocatorSequential {

    public AllocatorSequentialBestImpl(int size) throws NumberOutOfBoundsException {
        super(size);
        super.setSequentialFitMethod(new SequentialBestFitMethod());
    }

    public static class SequentialBestFitMethod implements AllocatorSequential.SequentialFitMethod {
        public int pickFreeBlock(int size, short[] memPool, MemHandle freelist) {
            System.out.println("(顺序适配方法——最佳适配)");
            if (freelist == null) return -1;  //无空闲块
            int freeStart = freelist.getPos();

            int bestPos = freeStart;
            int sizeOfBestPos = MAX_SHORT_INT;

            int curr = freeStart;
            do {
                if (memPool[curr + FULL_SIZE] >= (size + MIN_EXTRA) && memPool[curr + FULL_SIZE] < sizeOfBestPos) {
                    bestPos = curr;
                    sizeOfBestPos = memPool[bestPos + FULL_SIZE];
                }
                curr = memPool[curr + R_PTR];
            }
            while (curr != freeStart);

            if (memPool[bestPos + FULL_SIZE] >= (size + MIN_EXTRA)) return bestPos;
            else return -1;
        }
    }


}
