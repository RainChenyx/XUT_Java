/** 对某班学生成绩排序。从键盘依次输入某班学生的姓名和成绩（一个班级人数最多不超过50人）并保存，
 * 然后分别按学生成绩由高到低顺序输出学生姓名和成绩，成绩相同时，则按输入次序排序。
 */

package W3;

import java.util.Scanner;

public class order {
    public static void main(String[] args) {
        int n;
        String name;
        int score;
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        orderClass[] orders = new orderClass[50];
        for (int i = 0; i < n; i++)
        {
            name = scanner.next();
            score = scanner.nextInt();
            orders[i] = new orderClass(name, score);
        }
        //开始排序
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n - i - 1; j++)
            {
                if (orders[j].score < orders[j + 1].score)
                {
                    orderClass temp = orders[j];
                    orders[j] = orders[j + 1];
                    orders[j + 1] = temp;
                }
            }
        }
        for (int i = 0; i < n; i++)
        {
            orders[i].Print();
        }
    }
}
