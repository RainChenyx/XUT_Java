package W6;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class repeat {
    // distinct方法用于对列表进行去重
    public static <T> List<T> distinct(List<T> list) {
        LinkedHashSet<T> set = new LinkedHashSet<>(list);
        return new ArrayList<>(set);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 处理字符串输入
        System.out.println("请输入字符串的数量：");
        if (scanner.hasNextInt()) {
            int strCount = scanner.nextInt();
            scanner.nextLine(); // 消耗换行符
            System.out.println("请输入各个字符串，用空格分隔：");
            String line = scanner.nextLine().trim();
            String[] strArray = line.split("\\s+");

            if (strArray.length != strCount) {
                System.out.println("输入的字符串数量与指定数量不符");
            } else {
                List<String> strList = new ArrayList<>();
                for (String str : strArray) {
                    strList.add(str);
                }
                List<String> distinctStrList = distinct(strList);
                System.out.println("去重之后：");
                for (String str : distinctStrList) {
                    System.out.println(str);
                }
            }
        } else {
            System.out.println("输入的不是有效的整数，请重新运行程序并输入正确的数量。");
            scanner.close();
            return;
        }

        // 处理整数输入
        System.out.println("请输入整数的数量：");
        if (scanner.hasNextInt()) {
            int intCount = scanner.nextInt();
            scanner.nextLine(); // 消耗换行符
            System.out.println("请输入各个整数，用空格分隔：");
            String line2 = scanner.nextLine().trim();
            String[] intArray = line2.split("\\s+");

            if (intArray.length != intCount) {
                System.out.println("输入的整数数量与指定数量不符");
            } else {
                List<Integer> intList = new ArrayList<>();
                for (String numStr : intArray) {
                    try {
                        intList.add(Integer.parseInt(numStr));
                    } catch (NumberFormatException e) {
                        System.out.println("输入的内容不是有效的整数：" + numStr);
                        scanner.close();
                        return;
                    }
                }
                List<Integer> distinctIntList = distinct(intList);
                System.out.println("去重之后：");
                for (Integer num : distinctIntList) {
                    System.out.println(num);
                }
            }
        } else {
            System.out.println("输入的不是有效的整数，请重新运行程序并输入正确的数量。");
        }
        scanner.close();
    }
}