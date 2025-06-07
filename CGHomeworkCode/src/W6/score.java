package W6;

import java.util.ArrayList;
import java.util.Scanner;

public class score {
    public static void main(String[] args) {
        double sum = 0, max = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入全班人数：");
        int n = scanner.nextInt();
        scanner.nextLine();
        ArrayList<Double> scoreList = new ArrayList<>();
        System.out.println("请输入每位同学的数学成绩，用空格分隔，最后回车：");
        while (scoreList.size() < n) {
            String line = scanner.nextLine();
            String[] scoreStrArray = line.split("\\s+");
            for (String scoreStr : scoreStrArray) {
                try {
                    double score = Double.parseDouble(scoreStr);
                    scoreList.add(score);
                    if (score > max) {
                        max = score;
                    }
                    sum += score;
                } catch (NumberFormatException e) {
                    System.out.println("输入的内容不是有效的数字：" + scoreStr);
                }
            }
        }
        System.out.printf("最高分：%.1f\n", max);
        System.out.println("平均分：" + sum / n);
    }
}
