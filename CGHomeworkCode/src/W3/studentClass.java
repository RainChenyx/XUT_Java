package W3;

public class studentClass {
    public int id;
    public  String name;
    public int age;

    studentClass(int id,String name,int age){
        this.id=id;
        this.name=name;
        this.age=age;
    }
    public void addAge(){
        this.age++;
    }
    public void Print(){
        System.out.println("num:"+this.id+",name:"+this.name+",age:"+this.age);
    }
}
