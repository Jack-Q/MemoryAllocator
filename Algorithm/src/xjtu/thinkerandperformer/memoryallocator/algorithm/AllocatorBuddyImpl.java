package xjtu.thinkerandperformer.memoryallocator.algorithm;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This method implement a Buddy-Method for memory allocation.
 * <p>
 * Buddy method is to allocate the least size of the power of 2 from memory,
 */

class AllocatorBuddyImpl implements AllocatorADT {

    //region magic numbers
    // MEMORY STRUCTURE: [k] + [FreeList] + [Block]...[Block]
    private static final int MEMORY_K_OFFSET = 0;
    private static final int MEMORY_K_SIZE = 1;
    private static final int MEMORY_FREELIST_OFFSET = MEMORY_K_OFFSET + MEMORY_K_SIZE;
    private static final int MEMORY_FREELIST_SIZE = 30;
    private static final int MEMORY_START_OFFSET = MEMORY_FREELIST_OFFSET + MEMORY_FREELIST_SIZE;
    // BLOCK STRUCTURE: [Free/Used] + [Size] + [NextFree] + [PrevFree] + [MagicBegin]
    //                  + [Content]...[Content] + [MagicEnd]
    private static final int BLOCK_STATE_OFFSET = 0;
    private static final int BLOCK_SIZE_OFFSET = 1;
    private static final int BLOCK_NEXT_FREE_OFFSET = 2;
    private static final int BLOCK_PREV_FREE_OFFSET = 3;
    private static final int BLOCK_CONTENT_OFFSET = 5;
    private static final int BLOCK_EXTRA_SIZE = 6;
    private static final int MAGIC_BLOCK_BEGIN = 0xAAAA_AAAA;
    private static final int MAGIC_BLOCK_END = 0x5555_5555;
    private static final int MAGIC_BLOCK_FREE = 0xBBBB_BBBB;
    private static final int MAGIC_BLOCK_USED = 0x3333_3333;
    private static final int MAGIC_POSITION_NONE = -1;
    private static final int MAGIC_STRING_END = -1;
    private int[] memoryPool;

    public AllocatorBuddyImpl(int size) {
        init(size);
    }

    @Override
    public void init(int size) {
        int k = leastPowerOf2(size);
        // Create memory pool
        int memoryAlignedSize = pow2(k);
        int memorySize = MEMORY_START_OFFSET + memoryAlignedSize;
        memoryPool = new int[memorySize];

        // Set k
        memoryPool[MEMORY_K_OFFSET] = k;
        // Set up free block list
        initFreeBlock(MEMORY_START_OFFSET, k);
        Arrays.fill(memoryPool, MEMORY_FREELIST_OFFSET, MEMORY_FREELIST_SIZE, MAGIC_POSITION_NONE);
        setFreeList(k, MEMORY_START_OFFSET);
    }
    //endregion

    //region constants

    @Override
    public Variable newVariable(String variableName, int size) {
        int addr;
        int blockSize = size + BLOCK_EXTRA_SIZE;
        final int k = leastPowerOf2(blockSize);
        int currentK;
        // find the smallest available block
        for (currentK = k; currentK <= getMemoryK() && getFreeList(currentK) == MAGIC_POSITION_NONE; currentK++)
            ;
        // No suitable space left
        if (currentK > getMemoryK()) return null;
        // find the
        for (; currentK > k; currentK--) {
            // remove this from top of list;
            int largeBlock = getFreeList(currentK);
            setFreeList(currentK, getBlockNextFree(largeBlock));

            int firstBlock = largeBlock, secondBlock = largeBlock + pow2(currentK - 1);
            initFreeBlock(firstBlock, currentK - 1, secondBlock, MAGIC_POSITION_NONE);
            initFreeBlock(secondBlock, currentK - 1, MAGIC_POSITION_NONE, firstBlock);
            setFreeList(currentK - 1, firstBlock);
        }
        addr = getFreeList(k);
        setBlockState(addr, false);

        int blockNextFree = getBlockNextFree(addr);
        setBlockNextFree(addr, MAGIC_POSITION_NONE);

        setFreeList(k, blockNextFree);
        setBlockPrevFree(blockNextFree, MAGIC_POSITION_NONE);
        return addr == MAGIC_POSITION_NONE ? null : new Variable(new MemHandle(addr + BLOCK_CONTENT_OFFSET));
    }

    @Override
    public boolean write(Variable variable, String value) {

        int contentPos = variable.getHandle().getPos();
        int startPos = getBlockStartPos(contentPos);
        // Insufficient space
        if (getBlockSize(startPos) - BLOCK_EXTRA_SIZE >= value.length()) {

            for (int i = 0; i < value.length(); i++)
                memoryPool[contentPos + i] = value.charAt(i);
            if (getBlockSize(startPos) - BLOCK_EXTRA_SIZE > value.length())
                memoryPool[contentPos + value.length()] = MAGIC_STRING_END;
            return true;
        }
        return false;
    }

    @Override
    public String read(Variable variable) {
        int len = 0;
        while (memoryPool[variable.getHandle().getPos() + len] != MAGIC_STRING_END
                && memoryPool[variable.getHandle().getPos() + len] != MAGIC_BLOCK_END)
            len++;
        return new String(memoryPool, variable.getHandle().getPos(), len);
    }

    @Override
    public void deleteVariable(Variable variable) {
        int startPos = getBlockStartPos(variable.getHandle().getPos());
        int k = getBlockSize(startPos);

        // delete
        initFreeBlock(startPos, k, getFreeList(k));
        setFreeList(k, startPos);

        // merge
        while (k < getMemoryK() && getBlockSize(getBuddyPos(startPos, k)) == k && isFree(getBuddyPos(startPos, k))) {
            int buddy = getBuddyPos(startPos, k);
            int buddyPrev = getBlockPrevFree(buddy);
            if (buddyPrev != MAGIC_POSITION_NONE)
                setBlockNextFree(buddyPrev, getBlockNextFree(buddy));

            startPos = buddy < startPos ? buddy : startPos;
            k++;

            initFreeBlock(startPos, k, getFreeList(k));
        }


    }

    @Override
    public void show(AllocatorADT allocator, List<String> sortVariableList) {
        Iterator<String> iterator = sortVariableList.iterator();

        System.out.println("FreeList");
        for (int k = 1; k <= getMemoryK(); k++) {
            System.out.print(String.format(" %2d: %4s;", k, getFreeList(k) == MAGIC_POSITION_NONE ? "NULL" : getFreeList(k)));
            if (k % 6 == 0) System.out.println();
        }
        System.out.println();
        System.out.println("Memory Allocation: ");
        show(MEMORY_START_OFFSET, getMemoryK(), iterator);
    }

    private void show(int blockPosition, int k, Iterator<String> iterator) {
        if (getBlockSize(blockPosition) == k)
            System.out.println(String.format("%5d(%5d) : %c : %d %s",
                    blockPosition,
                    blockPosition - MEMORY_START_OFFSET,
                    isFree(blockPosition) ? 'F' : 'B',
                    k,
                    isFree(blockPosition) ? "" : ": " + iterator.next()));
        else {
            show(blockPosition, k - 1, iterator);
            show(blockPosition + pow2(k - 1), k - 1, iterator);
        }

    }

    //endregion

    //region private helper methods

    private int leastPowerOf2(int num) {
        int i = 0, j = 0;
        while (num > 1) {
            j |= num & 1;
            num >>>= 1;
            i++;
        }
        return i + j;
    }

    private int pow2(int k) {
        return 1 << k;
    }

    private boolean isFree(int startPos) {
        return memoryPool[startPos + BLOCK_STATE_OFFSET] == MAGIC_BLOCK_FREE;
    }

    private void setBlockState(int startPos, boolean isFree) {
        memoryPool[startPos + BLOCK_STATE_OFFSET] = isFree ? MAGIC_BLOCK_FREE : MAGIC_BLOCK_USED;
    }

    private void initFreeBlock(int startPos, int k) {
        initFreeBlock(startPos, k, MAGIC_POSITION_NONE, MAGIC_POSITION_NONE);
    }

    private void initFreeBlock(int startPos, int k, int nextFreeBlock) {
        initFreeBlock(startPos, k, nextFreeBlock, MAGIC_POSITION_NONE);
    }

    private void initFreeBlock(int startPos, int k, int nextFreeBlock, int prevFreeBlock) {
        memoryPool[startPos + BLOCK_STATE_OFFSET] = MAGIC_BLOCK_FREE;
        memoryPool[startPos + BLOCK_SIZE_OFFSET] = k;
        memoryPool[startPos + BLOCK_NEXT_FREE_OFFSET] = nextFreeBlock;
        memoryPool[startPos + BLOCK_PREV_FREE_OFFSET] = prevFreeBlock;

        memoryPool[startPos + BLOCK_CONTENT_OFFSET - 1] = MAGIC_BLOCK_BEGIN;
        memoryPool[startPos + pow2(k) - 1] = MAGIC_BLOCK_END;
    }

    private int getMemoryK() {
        return memoryPool[MEMORY_K_OFFSET];
    }


    private void setFreeList(int k, int position) {
        memoryPool[MEMORY_FREELIST_OFFSET + k] = position;
    }

    private int getFreeList(int k) {
        return memoryPool[MEMORY_FREELIST_OFFSET + k];
    }

    private int getBlockStartPos(int contentStartPos) {
        return contentStartPos - BLOCK_CONTENT_OFFSET;
    }

    private int getBlockSize(int startPos) {
        return memoryPool[startPos + BLOCK_SIZE_OFFSET];
    }

    private int getBlockNextFree(int startPos) {
        return memoryPool[startPos + BLOCK_NEXT_FREE_OFFSET];
    }

    private void setBlockNextFree(int startPos, int nextBlockFree) {
        memoryPool[startPos + BLOCK_NEXT_FREE_OFFSET] = nextBlockFree;
    }

    private int getBlockPrevFree(int startPos) {
        return memoryPool[startPos + BLOCK_PREV_FREE_OFFSET];
    }

    private void setBlockPrevFree(int startPos, int prevBlockFree) {
        memoryPool[startPos + BLOCK_PREV_FREE_OFFSET] = prevBlockFree;
    }

    private int getBuddyPos(int blockPos, int k) {
        return MEMORY_START_OFFSET +
                (blockPos - MEMORY_START_OFFSET) ^ (1 << k);
    }


    //endregion

}
