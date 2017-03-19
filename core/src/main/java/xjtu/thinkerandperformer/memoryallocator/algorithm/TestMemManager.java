package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.command.ICommand;
import xjtu.thinkerandperformer.memoryallocator.algorithm.command.Parser;

import java.util.Scanner;

public class TestMemManager {

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
