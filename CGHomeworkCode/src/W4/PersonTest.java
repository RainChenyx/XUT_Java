package W4;

class Person {
    public String name;
    public int age;
    public String sex;

    public Person(String name, int age, String sex)
    {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }
}

class Teacher extends Person{
    public String title;
    public String department;

    public Teacher(String name, int age, String sex, String Title, String Department)
    {
        super(name, age, sex);
        title = Title;
        department = Department;
    }

    @Override
    public String toString()
    {
        return name + "," + sex + "," + age + "," +title +  ","  + department;
    }
}

class Student extends Person{
    public String major;
    public int id;
    public String time;

    public Student(String name, int age, String sex, String Major, int Id, String Time)
    {
        super(name, age, sex);
        major = Major;
        id = Id;
        time = Time;
    }

    @Override
    public String toString()
    {
        return name + "," + sex + "," + age + "," +id +  ","  + time + "," + major;
    }
}

public class PersonTest {
    public static void main(String[] args) {
        Person p1 = new Teacher("Mary",45,"W","professor","computer");
        System.out.println(p1);
        Person p2 = new Student("John",20,"M","computer",923001,"2023/8/23");
        System.out.println(p2);
    }
}