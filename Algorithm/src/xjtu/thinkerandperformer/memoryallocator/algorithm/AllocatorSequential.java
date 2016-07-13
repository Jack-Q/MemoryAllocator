package xjtu.thinkerandperformer.memoryallocator.algorithm;

abstract class AllocatorSequential implements AllocatorADT {

    protected static final int START_TAG = 0;   // Start tag offset 偏移
    protected static final int FULL_SIZE = 1;   // Size field offset
    protected static final int USER_SIZE = 2;   // User size offset
    protected static final int L_PTR = 2;       // Left freelist pointer
    protected static final int R_PTR = 3;       // Right freelist pointer
    protected static final int DATA_POS = 3;    // Start of data
    protected static final int FREE = -1;      // Tag value
    protected static final int RESERVED = -2;  // Tag value
    protected static final int END_SIZE = 4;    // Size field offset
    protected static final int FREE_END_TAG = 5; // Tag field offset
    protected static final int RES_END_TAG = 3;  // Tag field offset
    protected static final int MIN_EXTRA = -2;  // Extra space needed
    protected static final int FREE_OVERHEAD = 6; // Number free fields overhead有间接费用、管理费用的意思
    protected static final int RES_OVERHEAD = 4;  // Number of res fields
    protected static final int MIN_REQUEST = 2; // Smallest data request
    protected static final int MAX_SHORTINT = 32767;
    protected static final int MIN_SHORTINT = -32768;

    short[] memPool;             // 存储池
    MemHandle freelist;         // 指向可利用空间表

    /*构造方法*/
    AllocatorSequential(int size) {
        if (size >= 6) init(size);
    }

    /*初始化存储池*/
    @Override
    public void init(int size) {
        memPool = new short[size];  // 申请和分配存储池
        freelist = new MemHandle(0); // Start of freelist

        memPool[START_TAG] = memPool[size - 1] = FREE; //给首尾两个标记位赋值为-1，即标记为空闲
        memPool[FULL_SIZE] = memPool[size - 2] = (short) (size - FREE_OVERHEAD);//给首尾的两个块长度单元赋值
        memPool[L_PTR] = memPool[R_PTR] = 0;
    }

    /*为变量申请空间*/
    @Override
    public Variable newVariable(String variableName, int size) {
        size = (size < MIN_REQUEST) ? MIN_REQUEST : size;

        int start = pickFreeBlock(size); //寻找空闲块 这里应该要有3种方法 顺序 最佳 最差
        if (start == -1) {
            System.out.println("空间不足！");
            return null; //未找到足够大的空闲块，就返回-1这个不存在的下标。此处可以考虑添加错误处理
        }

        if (memPool[start + FULL_SIZE] > (size + RES_OVERHEAD)) {  //？为何要加上overhead？空闲块的overhead不是有6个吗 肯定够用了啊
            // Fix up the remaining free space
            int oldSize = memPool[start + FULL_SIZE] - size - RES_OVERHEAD;
            memPool[start + oldSize + FREE_END_TAG] = FREE;//FREE_END_TAG==5
            memPool[start + oldSize + END_SIZE] = (short) oldSize; //END_SIZE == 4  给出末尾的块长度标记
            memPool[start + FULL_SIZE] = (short) oldSize; //重新给出开头的块长度标记 //注意 左右指针的值不用改动
            memPool[start + USER_SIZE] = 0;

            // Now, fix up the new block
            int newStart = start + memPool[start + FULL_SIZE] + FREE_OVERHEAD;//算了一下 确实是newStart的位置 它前面是分割走的一片空闲区域
            memPool[newStart + START_TAG] = RESERVED;//头部标记位
            memPool[newStart + FULL_SIZE] = (short) size;//完全可用的长度
            memPool[start + USER_SIZE] = 0;
            memPool[newStart + size + RES_END_TAG] = RESERVED;//尾部标记位 RES_END_TAG==3

            //return new Variable(variableName, new MemHandle(newStart));
            return new Variable(new MemHandle(newStart));
        } else { // Give over the whole block, remove from freelist

            // First, adjust the freelist
            if (memPool[start + R_PTR] == start)
                freelist = null;  // This is the last block
            else {
                memPool[memPool[start + R_PTR] + L_PTR] = memPool[start + L_PTR];//令 该空闲块右侧的空闲块 的左指针 指向 该空闲块的左侧的空闲块
                memPool[memPool[start + L_PTR] + R_PTR] = memPool[start + R_PTR];//令 该空闲块左侧的空闲块 的右指针 指向 该空闲块的右侧的空闲块
            }

            // Now, fill in the block
            memPool[start + START_TAG] = RESERVED;
            memPool[start + FULL_SIZE] += FREE_OVERHEAD - RES_OVERHEAD;//+6-4相当于+2 多了两块空出来了
            memPool[start + memPool[start + FULL_SIZE] + RES_END_TAG] = RESERVED;
            return new Variable(new MemHandle(start));
        }

    }

    /*查找空闲块*/
    abstract protected int pickFreeBlock(int size);

    /*向变量存储空间写入数据*/
    @Override
    public boolean write(Variable variable, String value) {
        int startPos = variable.getHandle().getPos();//该变量在存储池中的起点位置

        if (memPool[startPos + FULL_SIZE] < value.length()) {
            System.out.println("要写入的值的长度超限！");
            return false;//要写入的值的长度超限
        } else {
            memPool[startPos + USER_SIZE] = (short) value.length();
            for (int i = 0; i < value.length(); i++)
                memPool[startPos + DATA_POS + i] = (short) value.charAt(i);
            return true;
        }
    }

    /*从变量存储空间读数据*/
    @Override
    public String read(Variable variable) {
        int startPos = variable.getHandle().getPos();//该变量在存储池中的起点位置

        int length = memPool[startPos + USER_SIZE];
        int startData = startPos + DATA_POS;
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < length; i++) str.append((char) memPool[startData + i]);
        return new String(str);
    }

    /*删除变量*/
    @Override
    public void deleteVariable(Variable variable) {
        //定义F块常用量
        int startPos = variable.getHandle().getPos(); //该变量在存储池中的起点位置
        int size = memPool[startPos + FULL_SIZE];
        int endPos = startPos + size + RES_END_TAG;

        //存储池中已经没有空闲块，即将释放的占用块将成为唯一的一个空闲块
        if (freelist == null) {
            memPool[startPos] = memPool[endPos] = FREE;
            memPool[startPos + FULL_SIZE] = memPool[endPos - 1] = (short) (size + RES_OVERHEAD - FREE_OVERHEAD);
            memPool[startPos + L_PTR] = memPool[startPos + R_PTR] = (short) startPos;
            freelist = new MemHandle(startPos);
            variable.setHandle(null);
            return;
        }

        //搜索存储池中存在的空闲块
        int i = startPos;
        while (i > 0 && memPool[i] != FREE) i--;
        int j = endPos;
        while (j < memPool.length - 1 && memPool[j] != FREE) j++;

        //分类讨论：考虑只有一边存在空闲块的情况和两边都存在空闲块的情况
        if (!(memPool[i] == FREE && memPool[j] == FREE)) {

            //在F之前找到了一个空闲块P
            if (memPool[i] == FREE) {
                int endOfPreBlock = i;
                int sizeOfPreBlock = memPool[endOfPreBlock - 1];
                int startOfPreBlock = endOfPreBlock - FREE_END_TAG - sizeOfPreBlock;

                if (endOfPreBlock == startPos - 1) {//P与F相邻，合并二者
                    memPool[startOfPreBlock + FULL_SIZE] += size + RES_OVERHEAD;
                    memPool[endOfPreBlock] = memPool[startPos] = 0;//必须消除不再作为标记位的TAG
                    memPool[endPos] = FREE;
                    memPool[endPos - 1] = memPool[startOfPreBlock + FULL_SIZE];
                    //左右指针不必改动，因为是把F在右侧合并进去

                    //更新合并后的块的属性值
                    startPos = startOfPreBlock;
                    size = memPool[startOfPreBlock + FULL_SIZE];
                    endPos = endPos;
                } else {//P与F不相邻，指针互指
                    memPool[startPos + L_PTR] = (short) startOfPreBlock;
                    memPool[startPos + R_PTR] = memPool[startOfPreBlock + R_PTR];
                    memPool[startOfPreBlock + R_PTR] = memPool[memPool[startPos + R_PTR] + L_PTR] = (short) startPos;

                    memPool[startPos] = memPool[endPos] = FREE;
                    memPool[startPos + FULL_SIZE] = memPool[endPos - 1] = (short) (size + RES_OVERHEAD - FREE_OVERHEAD);
                }
            }

            //在F之后找到了一个空闲块S
            else if (memPool[j] == FREE) {
                int startOfSuccBlock = j;
                int sizeOfSuccBlock = memPool[startOfSuccBlock + FULL_SIZE];
                int endOfSuccBlock = startOfSuccBlock + sizeOfSuccBlock + FREE_END_TAG;

                if (startOfSuccBlock == endPos + 1) {//F与S相邻，合并二者
                    memPool[memPool[startOfSuccBlock + R_PTR] + L_PTR] = memPool[memPool[startOfSuccBlock + L_PTR] + R_PTR] = (short) startPos;

                    memPool[startPos] = FREE;
                    memPool[startPos + FULL_SIZE] += sizeOfSuccBlock + RES_OVERHEAD;
                    memPool[startPos + L_PTR] = memPool[startOfSuccBlock + L_PTR];
                    memPool[startPos + R_PTR] = memPool[startOfSuccBlock + R_PTR];
                    memPool[endPos] = memPool[startOfSuccBlock] = 0;//必须消除不再作为标记位的TAG
                    memPool[endOfSuccBlock - 1] = memPool[startPos + FULL_SIZE];

                    freelist = new MemHandle(startPos);//因为freelist可能是指向S块的

                } else {//F与S不相邻，指针互指
                    memPool[startPos + L_PTR] = memPool[startOfSuccBlock + L_PTR];
                    memPool[startPos + R_PTR] = (short) startOfSuccBlock;
                    memPool[startOfSuccBlock + L_PTR] = memPool[memPool[startPos + L_PTR] + R_PTR] = (short) startPos;

                    memPool[startPos] = memPool[endPos] = FREE;
                    memPool[startPos + FULL_SIZE] = memPool[endPos - 1] = (short) (size + RES_OVERHEAD - FREE_OVERHEAD);
                }
            }

            variable = null;
            return;

        } else {
            int endOfPreBlock = i;
            int sizeOfPreBlock = memPool[endOfPreBlock - 1];
            int startOfPreBlock = endOfPreBlock - FREE_END_TAG - sizeOfPreBlock;

            int startOfSuccBlock = j;
            int sizeOfSuccBlock = memPool[startOfSuccBlock + FULL_SIZE];
            int endOfSuccBlock = startOfSuccBlock + sizeOfSuccBlock + FREE_END_TAG;

            if ((endOfPreBlock == startPos - 1) && (endOfPreBlock == startPos - 1)) {
                memPool[endOfPreBlock] = memPool[startPos] = memPool[endPos] = memPool[startOfSuccBlock] = 0;
                memPool[startOfPreBlock + FULL_SIZE] = memPool[endOfSuccBlock - 1] = (short) (endOfSuccBlock - startOfPreBlock + 1 - FREE_OVERHEAD);
                memPool[startOfPreBlock + R_PTR] = memPool[startOfSuccBlock + R_PTR];
                memPool[memPool[sizeOfSuccBlock + R_PTR] + L_PTR] = (short) startOfPreBlock;

                freelist = new MemHandle(startOfPreBlock);//因为freelist可能是指向S块的

                variable.setHandle(null);
                return;
            }
            if ((endOfPreBlock == startPos - 1) && (endOfPreBlock != startPos - 1)) {
                memPool[startOfPreBlock + FULL_SIZE] += size + RES_OVERHEAD;
                memPool[endOfPreBlock] = memPool[startPos] = 0;//必须消除不再作为标记位的TAG
                memPool[endPos] = FREE;
                memPool[endPos - 1] = memPool[startOfPreBlock + FULL_SIZE];
                //左右指针不必改动，因为是把F在右侧合并进去

                variable.setHandle(null);
                return;
            }
            if ((endOfPreBlock != startPos - 1) && (endOfPreBlock == startPos - 1)) {
                memPool[memPool[startOfSuccBlock + R_PTR] + L_PTR] = memPool[memPool[startOfSuccBlock + L_PTR] + R_PTR] = (short) startPos;
                memPool[startPos] = FREE;
                memPool[startPos + FULL_SIZE] += sizeOfSuccBlock + RES_OVERHEAD;
                memPool[startPos + L_PTR] = memPool[startOfSuccBlock + L_PTR];
                memPool[startPos + R_PTR] = memPool[startOfSuccBlock + R_PTR];
                memPool[endPos] = memPool[startOfSuccBlock] = 0;//必须消除不再作为标记位的TAG
                memPool[endOfSuccBlock - 1] = memPool[startPos + FULL_SIZE];

                freelist = new MemHandle(startPos);//因为freelist可能是指向S块的
                memPool[startOfPreBlock + R_PTR] = memPool[memPool[startOfSuccBlock + R_PTR] + L_PTR] = (short) startPos;

                variable.setHandle(null);
                return;
            }
            if ((endOfPreBlock != startPos - 1) && (endOfPreBlock != startPos - 1)) {
                memPool[startPos] = memPool[endPos] = FREE;
                memPool[startPos + FULL_SIZE] = memPool[endPos - 1] = (short) (size + RES_OVERHEAD - FREE_OVERHEAD);
                memPool[L_PTR] = (short) startOfPreBlock;
                memPool[R_PTR] = (short) startOfSuccBlock;
                memPool[startOfPreBlock + R_PTR] = memPool[startOfSuccBlock + L_PTR] = (short) startPos;

                variable.setHandle(null);
                return;
            }
        }
    }


}
