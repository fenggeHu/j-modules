package fengge.tools;

import lombok.Getter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 把命令行解析成命令-参数形式
 * eg:
 * grep -rn -F 'Corba' main.log |grep "ObjName==M|N"|awk '{print $8}'|sort|uniq
 * cmd a>1 x>y
 *
 * @author max.hu  @date 2024/12/17
 **/
public class CommandLine {
    public static final char CMD_SPLIT = '|';
    public static final char DOUBLE_QUOTE = '"';
    public static final char SINGLE_QUOTE = '\'';
    public static final String SPACE = " ";
    @Getter
    String command; // 命令
    @Getter
    Map<String, String> options;  // 选项：-rn -F "xxx"
    @Getter
    List<String> arguments; // 参数

    public CommandLine(String command, Map<String, String> options, List<String> arguments) {
        this.command = command;
        this.options = options;
        this.arguments = arguments;
    }

    // 取option的值
    public String option(String key, String def) {
        String v = this.options.get(key);
        return v == null ? def : v;
    }

    // 解析命令行 - 命令行有顺序
    // eg: grep -rn -F "Corba" main.log |grep "ObjName==M|N"|awk '{print $8}'|sort|uniq
    public static List<CommandLine> parse(String commandLine) {
        List<CommandLine> result = new LinkedList<>();
        List<String> commands = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideDoubleQuote = false;
        boolean insideSingleQuote = false;

        for (int i = 0; i < commandLine.length(); i++) {
            char c = commandLine.charAt(i);
            // 检查是否遇到双引号或单引号
            if (c == DOUBLE_QUOTE && !insideSingleQuote) {
                insideDoubleQuote = !insideDoubleQuote;  // 切换双引号状态
            } else if (c == SINGLE_QUOTE && !insideDoubleQuote) {
                insideSingleQuote = !insideSingleQuote;  // 切换单引号状态
            }

            // 如果当前字符是分隔符，并且不在引号内，进行分割
            if (c == CMD_SPLIT && !insideDoubleQuote && !insideSingleQuote) {
                commands.add(current.toString());
                current.setLength(0);  // 清空 StringBuilder
            } else {
                current.append(c);  // 否则，继续累加字符
            }
        }

        // 添加最后一个部分
        commands.add(current.toString());

        // 解析每个命令
        for (String cmd : commands) {
            result.add(of(cmd));
        }
        return result;
    }

    // 解析一个命令
    // eg: grep -rn -F "Corba" main.log
    public static CommandLine of(String cmdString) {
        List<String> tokens = getTokens(cmdString);

        String command = tokens.get(0);  // 第一个部分是命令本身
        if (tokens.size() > 1) {
            CommandLine cl = parseArgs(tokens.subList(1, tokens.size()));
            cl.command = command;
            return cl;
        } else {
            return new CommandLine(command, Collections.emptyMap(), Collections.emptyList());
        }
    }

    // 解析 --dir=/Users/max --project=customer
    public static CommandLine parseArgs(String[] args) {
        Map<String, String> options = new LinkedHashMap<>();
        List<String> arguments = new ArrayList<>();
        for (String arg : args) {
            int eqi = arg.indexOf("=");
            if (eqi > 0) {
                String left = arg.substring(0, eqi);
                if (left.charAt(0) == '-') {
                    if (left.length() > 2 && left.charAt(1) == '-') {
                        left = left.substring(2);
                    } else {
                        left = left.substring(1);
                    }
                }
                String right = arg.substring(eqi + 1);
                options.put(left, right);
            } else {
                arguments.add(arg);
            }
        }

        return new CommandLine(null, options, arguments);
    }

    // 解析参数. -rn -F "Corba" main.log
    public static CommandLine parseArgs(String args) {
        List<String> tokens = getTokens(args);
        return parseArgs(tokens);
    }

    // 解析参数
    private static CommandLine parseArgs(List<String> tokens) {
        Map<String, String> options = new LinkedHashMap<>();
        List<String> arguments = new ArrayList<>();

        String currentOption = null;  // 当前处理的选项
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            // 处理选项（以 "-" 开头的部分）
            if (token.startsWith("-")) {
                // 如果是复合选项（如 -rn），保持其原样作为一个选项
                if (currentOption != null) {
                    options.put(currentOption, null);
                }
                currentOption = token;
            } else {
                // 如果当前选项有值，则与该选项关联
                if (currentOption != null) {
                    options.put(currentOption, token);
                    currentOption = null;  // 处理完当前选项，清空当前选项
                } else {
                    // 无选项的参数（如文件名）
                    arguments.add(token);
                }
            }
        }
        if (null != currentOption) {    // 最后一项option
            options.put(currentOption, null);
        }

        return new CommandLine(null, options, arguments);
    }

    // 使用正则表达式拆分命令行，保留引号内的内容作为一个整体
    private static Pattern pattern = Pattern.compile("'([^']*)'|\"([^\"]*)\"|([^\\s]+)");

    private static List<String> getTokens(String cmdString) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = pattern.matcher(cmdString);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // 处理单引号内的内容
                tokens.add(SINGLE_QUOTE + matcher.group(1) + SINGLE_QUOTE);
            } else if (matcher.group(2) != null) {
                // 处理双引号内的内容
                tokens.add(DOUBLE_QUOTE + matcher.group(2) + DOUBLE_QUOTE);
            } else {
                // 处理没有引号的内容
                tokens.add(matcher.group(3));
            }
        }
        return tokens;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (null != command) sb.append(command);
        if (null != options) {
            options.forEach((k, v) -> {
                sb.append(SPACE).append(k);
                if (null != v) {
                    sb.append(SPACE).append(v);
                }
            });
        }
        if (null != arguments) {
            arguments.forEach(v -> sb.append(SPACE).append(v));
        }
        return sb.toString();
    }

}
