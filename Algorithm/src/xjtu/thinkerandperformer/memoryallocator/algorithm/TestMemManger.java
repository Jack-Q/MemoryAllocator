package xjtu.thinkerandperformer.memoryallocator.algorithm;

import java.util.Scanner;

public class TestMemManger {

    public static void main(String[] args) {
        MemManager mmg = new MemManager();
        Scanner input = new Scanner(System.in);

        for (; ; ) {
            String primitiveCommand = input.nextLine();
            try {
                ICommand command = Parser.parse(primitiveCommand);
                command.invoke(mmg);
                mmg.show();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
