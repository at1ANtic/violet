package cn.atlantt1c.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumberUtil {

    // 接受输入字符串并返回数字列表的方法
    public static List<Integer> extractNumbers(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        List<Integer> numbers = new ArrayList<>();

        // 找到所有匹配的数字并将其转换为 Integer 添加到列表
        while (matcher.find()) {
            numbers.add(Integer.parseInt(matcher.group()));
        }

        return numbers;
    }

    // 将 List<Integer> 转换为 List<String>，每个数字带双引号
    public static String convertNumbersToQuotedStrings(List<Integer> numbers) {
        return numbers.stream()
                .map(number -> "\"" + number + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }
}

