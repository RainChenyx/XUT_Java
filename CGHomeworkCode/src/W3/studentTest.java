package W3;

import java.util.Scanner;

public class studentTest {
    public static void main(String[] args) {
        studentClass[] students = {
            new studentClass(1,"zhang",19),
            new studentClass(2,"wang",21),
            new studentClass(3,"li",20),
            new studentClass(4,"yang",18)
        };
        for(int i=0;i<students.length;i++)
            students[i].addAge();
        for(studentClass student:students){
            student.Print();
        }
        for(int i=0;i<students.length;i++){
            if(students[i].age>20)
                students[i].Print();
        }
    }
}