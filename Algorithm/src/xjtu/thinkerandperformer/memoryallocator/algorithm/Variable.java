package xjtu.thinkerandperformer.memoryallocator.algorithm;


class Variable{
    private final String name;
    private final Allocator allocator;
    private int address;

    public Variable(String name, Allocator allocator){
        this.name = name;
        this.allocator = allocator;
    }

    public Variable(String name, Allocator allocator, int address){
        this(name, allocator);
        setAddress(address);
    }

    public void setAddress(int address){
        this.address = address;
    }

    public int getAddress(){return this.address;}

    public String getValue(){
        return allocator.read(this);
    }

    public boolean setValue(String value){
        return allocator.write(this, value);
    }

    public void delete(){
        allocator.deleteVariable(this);
    }


    @Override
    public String toString(){
        return getValue();
    }

    public String getName(){return this.name;}

}
