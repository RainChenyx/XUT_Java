package W3;

public class orderClass {
    public String name;
    public int score;
    public orderClass(String name,int score)
    {
        this.name = name;
        this.score = score;
    }
    public void Print()
    {
        System.out.printf("%15s%5d\n",name,score);
    }
}
