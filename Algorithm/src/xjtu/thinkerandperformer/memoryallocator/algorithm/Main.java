package xjtu.thinkerandperformer.memoryallocator.algorithm;

public class Main {
    static void l(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        l("Buddy Implementation: ");
        TestAllocator(new AllocatorBuddyImpl());
        l("\n\n");

//        l("Sequential (First Fit) Implementation: ");
//        TestAllocator(new AllocatorSequentialFirstImpl());
//        l("\n\n");

    }

    private static void TestAllocator(Allocator allocator) {
        // init 2000
        allocator.init(2000);
        allocator.show();

        // new abc=24
        l("abc=24");
        Variable abc = allocator.newVariable("abc", 24);
        allocator.show();

        // new bcd=123
        l("bcd=123");
        Variable bcd = allocator.newVariable("bcd", 123);
        allocator.show();

        // new cde=41
        l("cde=41");
        Variable cde = allocator.newVariable("cde", 41);
        allocator.show();

        // write abc="Hello"
        l("write abc=\"Hello\"");
        allocator.write(abc, "Hello");

        // read abc
        l("read abc");
        l(allocator.read(abc));

        // delete abc
        l("delete abc");
        allocator.deleteVariable(abc);
        allocator.show();

        // delete bcd
        l("delete bcd");
        allocator.deleteVariable(bcd);
        allocator.show();

        // delete cde
        l("delete cde");
        allocator.deleteVariable(cde);
        allocator.show();
    }
}
