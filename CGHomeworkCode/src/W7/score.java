package W7;

import java.io.*;
import java.util.Scanner;
import java.lang.*;

public class score
{
    static class Student
    {
        public String id;
        public String name;
        public int score_math;
        public int score_english;
        public int score_java;

        public Student(String id, String name, int score_math, int score_english, int score_java)
        {
            this.id = id;
            this.name = name;
            this.score_math = score_math;
            this.score_english = score_english;
            this.score_java = score_java;
        }

        public String toString()
        {
            return String.format("%s %s %d %d %d", id, name, score_math, score_english, score_java);
        }
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        Student[] students = new Student[10];
        for (int i = 0; i < 10; i++)
        {
            String str = scanner.nextLine().trim();
            String[] part = str.split("\\s+");
            int score_math = Integer.parseInt(part[2]);
            int score_english = Integer.parseInt(part[3]);
            int score_java = Integer.parseInt(part[4]);
            students[i] = new Student(part[0], part[1], score_math, score_english, score_java);
        }

        // 写入student1.txt文件
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("student1.txt")))
        {
            for(Student student : students)
            {
                writer.write(student.toString());
                writer.newLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // 读取student1.txt文件
        try(BufferedReader reader = new BufferedReader(new FileReader("student1.txt")))
        {
            String line;
            double sum;
            double ave;
            double ave_math = 0;
            while((line = reader.readLine()) != null)
            {
                sum = 0;
                String[] str = line.split("\\s+");
                for (int i = 2; i < str.length; i++) {
                    sum += Integer.parseInt(str[i]);
                    if(i == 2)
                        ave_math += Integer.parseInt(str[2]);
                }
                ave = sum / (str.length - 2);
                if (ave == (int) ave)
                    System.out.printf("学号：%s，姓名：%s，平均分：%.1f\n", str[0], str[1], ave);
                else
                    System.out.printf("学号：%s，姓名：%s，平均分：%.14f\n", str[0], str[1], ave);
            }
            System.out.printf("全部学生的数学平均分：%.1f\n", ave_math/10);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
