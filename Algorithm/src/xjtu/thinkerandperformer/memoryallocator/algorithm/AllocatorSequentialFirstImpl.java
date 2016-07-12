package xjtu.thinkerandperformer.memoryallocator.algorithm;


public class AllocatorSequentialFirstImpl implements Allocator {
    private int[] memoryPool;

    @Override
    public void init(int size) {
        memoryPool = new int[size];
        setMemoryFirstFree(MEMORY_START_OFFSET);
        setMemoryNextFree(MEMORY_START_OFFSET);
        initBlock(MEMORY_START_OFFSET, size - MEMORY_START_OFFSET, MAGIC_POSITION_NONE, MAGIC_POSITION_NONE);
    }

    @Override
    public Variable newVariable(String variableName, int contentSize) {
        int blockSize = contentSize + BLOCK_EXTRA_SIZE;

        // Get the first block large enough provide size of `blockSize`
        int addr = findNextFreeBlock(blockSize);
        if (addr == MAGIC_POSITION_NONE) return null;

        if (getBlockSize(addr) - blockSize < BLOCK_SIZE_THRESHOLD) {
            // allocate the whole block
            int next = getNextFreeBlock(addr);
            int prev = getPrevFreeBlock(addr);
            setMemoryNextFree(next);
            if (getMemoryFirstFree() == addr) setMemoryFirstFree(next);

            // remove the block from free block list
            if (next != MAGIC_POSITION_NONE) setPrevFreeBlock(next, prev);
            if (prev != MAGIC_POSITION_NONE) setNextFreeBlock(prev, next);

        } else {
            // split the block and return the first part
            int latterSize = getBlockSize(addr) - blockSize;
            int latter = addr + blockSize;
            int next = getNextFreeBlock(addr);
            int prev = getPrevFreeBlock(addr);

            setMemoryNextFree(latter);
            if (getMemoryFirstFree() == addr) setMemoryFirstFree(latter);

            initBlock(addr, blockSize, MAGIC_POSITION_NONE, MAGIC_POSITION_NONE);
            initBlock(latter, latterSize, next, prev);
            // replace the block from free block list by the latter part
            if (next != MAGIC_POSITION_NONE) setPrevFreeBlock(next, latter);
            if (prev != MAGIC_POSITION_NONE) setNextFreeBlock(prev, latter);

        }

        // set current to used
        setBlockState(addr, false);

        return new Variable(variableName, this, addr + BLOCK_CONTENT_OFFSET);
    }

    @Override
    public boolean write(Variable variable, String value) {

        int contentPos = variable.getAddress();
        int startPos = getBlockStartPos(contentPos);
        if (getBlockSize(startPos) - BLOCK_EXTRA_SIZE < value.length())
            return false;
        for (int i = 0; i < value.length(); i++)
            memoryPool[contentPos + i] = value.charAt(i);
        if (getBlockSize(startPos) - BLOCK_EXTRA_SIZE > value.length())
            memoryPool[contentPos + value.length()] = MAGIC_STRING_END;
        return true;
    }

    @Override
    public String read(Variable variable) {
        int len = 0;
        int startPos = getBlockStartPos(variable.getAddress());
        while (memoryPool[variable.getAddress() + len] != MAGIC_STRING_END
                && variable.getAddress() + len < startPos + getBlockSize(startPos))
            len++;
        return new String(memoryPool, variable.getAddress(), len);
    }

    @Override
    public void deleteVariable(Variable variable) {
        int startPos = getBlockStartPos(variable.getAddress());
        int prevPos = hasPrevBlock(startPos) ? prevBlock(startPos) : MAGIC_POSITION_NONE;
        int nextPos = hasNextBlock(startPos) ? nextBlock(startPos) : MAGIC_POSITION_NONE;

        if (hasPrevBlock(startPos) && isBlockFree(prevPos) && hasNextBlock(startPos) && isBlockFree(nextPos)) {
            // Merge three part
            removeBlock(prevPos);
            removeBlock(nextPos);

            if (getMemoryFirstFree() != MAGIC_POSITION_NONE) setPrevFreeBlock(getMemoryFirstFree(), prevPos);
            initBlock(prevPos, getBlockSize(prevPos) + getBlockSize(startPos) + getBlockSize(nextPos),
                    getMemoryFirstFree(), MAGIC_POSITION_NONE);
            setMemoryFirstFree(prevPos);
            setMemoryNextFree(prevPos);
        } else if (hasPrevBlock(startPos) && isBlockFree(prevPos)) {
            // Merge current with prev
            removeBlock(prevPos);

            if (getMemoryFirstFree() != MAGIC_POSITION_NONE) setPrevFreeBlock(getMemoryFirstFree(), startPos);
            initBlock(prevPos, getBlockSize(prevPos) + getBlockSize(startPos),
                    getMemoryFirstFree(), MAGIC_POSITION_NONE);
            setMemoryFirstFree(prevPos);
        } else if (hasNextBlock(startPos) && isBlockFree(nextPos)) {
            // Merge current with next
            removeBlock(nextPos);

            if (getMemoryFirstFree() != MAGIC_POSITION_NONE) setPrevFreeBlock(getMemoryFirstFree(), startPos);
            initBlock(startPos, getBlockSize(startPos) + getBlockSize(nextPos),
                    getMemoryFirstFree(), MAGIC_POSITION_NONE);
            setMemoryFirstFree(startPos);
            setMemoryNextFree(startPos);
        } else {
            // no merge
            if (getMemoryFirstFree() != MAGIC_POSITION_NONE) setPrevFreeBlock(getMemoryFirstFree(), startPos);
            initBlock(startPos, getBlockSize(startPos), getMemoryFirstFree(), MAGIC_POSITION_NONE);
            setMemoryFirstFree(startPos);
            setMemoryFirstFree(startPos);
        }


    }


    @Override
    public void show() {
        System.out.println(String.format("First: %4d; Next: %4d", getMemoryFirstFree(), getMemoryNextFree()));
        for (int pos = MAGIC_POSITION_NONE; hasNextBlock(pos); ) {
            pos = nextBlock(pos);
            System.out.println(String.format("%4d : %c : %4d", pos, isBlockFree(pos) ? 'F' : 'B', getBlockSize(pos)));
        }
    }

    /**
     * Memory Pool Structure
     * [MetaPart] + [Block]...[Block]
     * MetaPart: [FirstFree] + [NextFree]
     * FirstFree: first position of free block
     * NextFree: next position for allocation
     * <p>
     * Block: [Status] + [Size] + [NextFree] + [PrevFree]
     * + [Content]...[Content] + [Size]
     */

    // region Constant
    //region Memory
    private static final int MEMORY_META_OFFSET = 0;
    private static final int MEMORY_FIRST_FREE = MEMORY_META_OFFSET + 0;
    private static final int MEMORY_NEXT_FREE = MEMORY_META_OFFSET + 1;
    private static final int MEMORY_META_SIZE = 2;
    private static final int MEMORY_START_OFFSET = MEMORY_META_OFFSET + MEMORY_META_SIZE;
    //endregion Memory
    // region Block
    private static final int BLOCK_STATE_OFFSET = 0;
    private static final int BLOCK_SIZE_OFFSET = 1;
    private static final int BLOCK_NEXT_FREE_OFFSET = 2;
    private static final int BLOCK_PREV_FREE_OFFSET = 3;
    private static final int BLOCK_CONTENT_OFFSET = 4;
    private static final int BLOCK_EXTRA_SIZE = 6;
    // left at least 3 *bytes* for a block, otherwise, allocate this block all to the request
    private static final int BLOCK_SIZE_THRESHOLD = BLOCK_EXTRA_SIZE + 3;
    // endregion Block
    // region Magic number
    private static final int MAGIC_BLOCK_FREE = 0xBBBB_BBBB;
    private static final int MAGIC_BLOCK_USED = 0x3333_3333;
    /*Subclass*/ static final int MAGIC_POSITION_NONE = -1;
    private static final int MAGIC_STRING_END = -1;
    // endregion Magic number
    // endregion Constant


    //region Helper method

    private void initBlock(int blockStart, int size, int nextFreePosition, int prevFreePosition) {
        memoryPool[blockStart + BLOCK_STATE_OFFSET] = MAGIC_BLOCK_FREE;
        memoryPool[blockStart + BLOCK_SIZE_OFFSET] = size;
        memoryPool[blockStart + BLOCK_NEXT_FREE_OFFSET] = nextFreePosition;
        memoryPool[blockStart + BLOCK_PREV_FREE_OFFSET] = prevFreePosition;
        // Tailing Size tag
        memoryPool[blockStart + size - 1] = size;
    }

    private void setMemoryNextFree(int position) {
        memoryPool[MEMORY_NEXT_FREE] = position;
    }

    private int getMemoryNextFree() {
        return memoryPool[MEMORY_NEXT_FREE];
    }

    private void setMemoryFirstFree(int position) {
        memoryPool[MEMORY_FIRST_FREE] = position;
    }

    int getMemoryFirstFree() {
        return memoryPool[MEMORY_FIRST_FREE];
    }

    int getBlockSize(int blockPos) {
        return memoryPool[blockPos + BLOCK_SIZE_OFFSET];
    }

    private boolean isBlockFree(int blockPos) {
        return memoryPool[blockPos + BLOCK_STATE_OFFSET] == MAGIC_BLOCK_FREE;
    }

    private void setBlockState(int blockPos, boolean isFree) {
        memoryPool[blockPos + BLOCK_STATE_OFFSET] = isFree ? MAGIC_BLOCK_FREE : MAGIC_BLOCK_USED;
    }

    private boolean hasNextBlock(int blockPos) {
        return blockPos == MAGIC_POSITION_NONE
                || blockPos + getBlockSize(blockPos) < memoryPool.length;
    }

    private int nextBlock(int blockPos) {
        if (blockPos == MAGIC_POSITION_NONE) return MEMORY_START_OFFSET;
        else return blockPos + getBlockSize(blockPos);
    }

    private boolean hasPrevBlock(int blockPos) {
        return blockPos == MAGIC_POSITION_NONE
                || blockPos > MEMORY_START_OFFSET;
    }

    private int prevBlock(int blockPos) {
        if (blockPos == MAGIC_POSITION_NONE) return memoryPool.length - memoryPool[memoryPool.length - 1];
        else return blockPos - memoryPool[blockPos - 1];
    }

    private void setNextFreeBlock(int blockPos, int nextFreeBlockPos) {
        memoryPool[blockPos + BLOCK_NEXT_FREE_OFFSET] = nextFreeBlockPos;
    }

    int getNextFreeBlock(int blockPos) {
        return memoryPool[blockPos + BLOCK_NEXT_FREE_OFFSET];
    }

    private void setPrevFreeBlock(int blockPos, int prevFreeBlockPos) {
        memoryPool[blockPos + BLOCK_PREV_FREE_OFFSET] = prevFreeBlockPos;
    }

    private int getPrevFreeBlock(int blockPos) {
        return memoryPool[blockPos + BLOCK_PREV_FREE_OFFSET];
    }


    int findNextFreeBlock(int blockSize) {

        // Seek from the next free
        for (int pos = getMemoryNextFree(); pos != MAGIC_POSITION_NONE; pos = getNextFreeBlock(pos))
            if (getBlockSize(pos) >= blockSize)
                return pos;

        // Restart seeking from the head of the list
        for (int pos = getMemoryFirstFree();
             pos != MAGIC_POSITION_NONE && pos != getMemoryNextFree();
             pos = getNextFreeBlock(pos))
            if (getBlockSize(pos) >= blockSize)
                return pos;

        return MAGIC_POSITION_NONE;
    }

    private int removeBlock(int blockPos) {
        if (blockPos == getMemoryFirstFree())
            setMemoryFirstFree(getNextFreeBlock(blockPos));
        if (getPrevFreeBlock(blockPos) != MAGIC_POSITION_NONE)
            setNextFreeBlock(getPrevFreeBlock(blockPos), getNextFreeBlock(blockPos));
        if (getNextFreeBlock(blockPos) != MAGIC_POSITION_NONE)
            setPrevFreeBlock(getNextFreeBlock(blockPos), getPrevFreeBlock(blockPos));
        return 0;
    }

    private int getBlockStartPos(int contentPosition) {
        return contentPosition - BLOCK_CONTENT_OFFSET;
    }

    //endregion Helper method

}
