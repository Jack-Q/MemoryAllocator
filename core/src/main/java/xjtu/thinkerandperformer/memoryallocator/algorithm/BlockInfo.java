package xjtu.thinkerandperformer.memoryallocator.algorithm;


public class BlockInfo {
    private final int startPos;
    private final int size;
    private final int prevPosition;
    private final int nextPosition;
    private final boolean isFree;
    private final String variableName;

    public BlockInfo(int startPos, int size, int prevPosition, int nextPosition, boolean isFree, String variableName) {
        this.startPos = startPos;
        this.size = size;
        this.prevPosition = prevPosition;
        this.nextPosition = nextPosition;
        this.isFree = isFree;
        this.variableName = variableName;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getSize() {
        return size;
    }

    public int getPrevPosition() {
        return prevPosition;
    }

    public int getNextPosition() {
        return nextPosition;
    }

    public boolean isFree() {
        return isFree;
    }

    public String getVariableName() {
        return variableName;
    }
}
