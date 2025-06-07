package W7;

import java.io.*;
import java.util.Scanner;

class StudentA {
    public String name;
    public int age;
    public int id;

    StudentA(String name, int age, int id){
        this.name = name;
        this.age = age;
        this.id = id;
    }

    @Override
    public String toString(){
        return "Student name=" + name + ", age=" + age + ", id=:" + id;
    }
}

public class student{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        scanner.nextLine();
        StudentA[] students = new StudentA[number];
        for(int i = 0; i < number; i++){
            String str = scanner.nextLine().trim();
            String[] part = str.split("\\s+");
            String name = part[0];
            int age = Integer.parseInt(part[1]);
            int id = Integer.parseInt(part[2]);
            students[i] = new StudentA(name, age, id);
        }
        for(int i = 0; i < number; i++){
            System.out.println(students[i]);
        }
        // 写入文件
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("student.txt")))
        {
            for(StudentA student : students)
            {
                writer.write(student.toString());
                writer.newLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // 读取文件
        try(BufferedReader reader = new BufferedReader(new FileReader("student.txt")))
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
