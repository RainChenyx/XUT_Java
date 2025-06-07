package W6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

class Student{
    public String name;
    public int age;
    public String gender;

    public Student(String name, int age, String gender)
    {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
    public String toString()
    {
        return name + "\t" + age + "\t" + gender;
    }
}

class student {
    static List<Student> convert(List<Map<String, Object>> studentMaps) {
        List<Student> students = new ArrayList<>();
        for (Map<String, Object> map : studentMaps) {
            String name = (String) map.get("name");
            int age = (int) map.get("age");
            String gender = (String) map.get("gender");
            students.add(new Student(name, age, gender));
        }
        return students;
    }
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入学生人数：");
        int n = scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入每位同学信息，每个同学一行");
        System.out.println("每行上的内容依次为：姓名、年龄、性别。用空格分隔：");
        List<Map<String, Object>> studentMaps = new ArrayList<>();
        for(int i = 0; i < n; i++)
        {
            String[] info = scanner.nextLine().split(" ");
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("name", info[0]);
            studentMap.put("age", Integer.parseInt(info[1]));
            studentMap.put("gender", info[2]);
            studentMaps.add(studentMap);
        }
        for (Map<String, Object> map : studentMaps) {
            System.out.println(map.get("name") + "\t" + map.get("age") + "\t" + map.get("gender"));
        }

        List<Student> students = convert(studentMaps);
        for (Student student : students) {
            System.out.println(student);
        }
        scanner.close();
    }
}
