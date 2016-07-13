package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.IllegalCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {

    public static ICommand parse(String primitiveCommand) throws IllegalCommandException {
        String command = " " + primitiveCommand;    //强制在命令头部添加空格
        String[] parser = command.split("[\"=\\s]+");
        if (parser.length < 2) throw new IllegalCommandException("未检测到输入");    //命令只有空格的错误情况

        switch (parser[1]) {
            case "init": {
                try {
                    if (parser.length != 3) throw new IllegalCommandException("init命令格式错误");
                    int memPoolSize = Integer.parseInt(parser[2]);
                    return new InitCommand(memPoolSize);
                } catch (Exception ex) {
                    if (ex instanceof NumberFormatException) throw new NumberFormatException("数字格式错误");
                    else throw ex;
                }

            }
            case "new": {
                try {
                    if (parser.length != 4) throw new IllegalCommandException("new命令格式错误");
                    String varName = parser[2];
                    int varSize = Integer.parseInt(parser[3]);
                    return new NewCommand(varName, varSize);
                } catch (Exception ex) {
                    if (ex instanceof NumberFormatException) throw new IllegalCommandException("无法识别的数字");
                    else throw ex;
                }

            }
            case "delete": {
                try {
                    if (parser.length != 3) throw new IllegalCommandException("delete命令格式错误");
                    String varName = parser[2];
                    return new DeleteCommand(varName);
                } catch (IllegalCommandException ex) {
                    throw ex;
                }

            }
            case "write": {
                try {
                    Pattern compile = Pattern.compile("\\s*write\\s+([A-Za-z]\\w*)\\s*=\\s*\"((\\\\[\"\\\\']|[^\\\\\"])*)\"\\s*");
                    Matcher matcher = compile.matcher(command);
                    if (!matcher.find()) throw new IllegalCommandException("write命令格式错误");
                    String varName = matcher.group(1);
                    String varValue = matcher.group(2).replaceAll("\\\\(.)", "$1");
                    return new WriteCommand(varName, varValue);
                } catch (IllegalCommandException ex) {
                    throw ex;
                }

            }
            case "read": {
                try {
                    if (parser.length != 3) throw new IllegalCommandException("read命令格式错误");
                    String varName = parser[2];
                    return new ReadCommand(varName);
                } catch (IllegalCommandException ex) {
                    throw ex;
                }

            }
            default: {
                throw new IllegalCommandException();
            }

        }

    }

}

