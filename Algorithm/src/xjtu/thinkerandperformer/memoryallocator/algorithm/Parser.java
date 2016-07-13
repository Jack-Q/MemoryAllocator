package xjtu.thinkerandperformer.memoryallocator.algorithm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {

    public static ICommand parse(String primitiveCommand) throws Exception {
        String command = " " + primitiveCommand;    //强制在命令头部添加空格
        String[] parser = command.split("[\"=\\s]+");
        if (parser.length < 2) throw new Exception("未检测到输入");         //命令只有空格的错误情况
        switch (parser[1]) {
            case "init": {
                try {
                    if (parser.length != 3) throw new Exception();
                    int memPoolSize = Integer.parseInt(parser[2]);
                    return new InitCommand(memPoolSize);
                } catch (Exception ex) {
                    if (ex instanceof NumberFormatException) throw new Exception("无法识别的数字");
                    else throw new Exception("init命令错误");
                }

            }
            case "new": {
                try {
                    if (parser.length != 4) throw new Exception();
                    String varName = parser[2];
                    int varSize = Integer.parseInt(parser[3]);
                    return new NewCommand(varName, varSize);
                } catch (Exception ex) {
                    if (ex instanceof NumberFormatException) throw new Exception("无法识别的数字");
                    else throw new Exception("new命令错误");
                }

            }
            case "delete": {
                try {
                    if (parser.length != 3) throw new Exception();
                    String varName = parser[2];
                    return new DeleteCommand(varName);
                } catch (Exception ex) {
                    throw new Exception("delete命令错误");
                }

            }
            case "write": {
                try {
                    Pattern compile = Pattern.compile("\\s*write\\s+([A-Za-z]\\w*)\\s*=\\s*\"((\\\\[\"\\\\']|[^\\\\\"])*)\"\\s*");
                    Matcher matcher = compile.matcher(command);
                    if (!matcher.find()) throw new Exception();
                    String varName = matcher.group(1);
                    String varValue = matcher.group(2).replaceAll("\\\\(.)", "$1");
                    return new WriteCommand(varName, varValue);
                } catch (Exception ex) {
                    throw new Exception("write命令错误");
                }

            }
            case "read": {
                try {
                    if (parser.length != 3) throw new Exception();
                    String varName = parser[2];
                    return new ReadCommand(varName);
                } catch (Exception ex) {
                    throw new Exception("read命令错误");
                }

            }
            default: {
                throw new Exception("无法识别的命令");
            }

        }

    }

}

