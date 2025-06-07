package W3;

public class triangleClass {
    public double a,b,c;
    public triangleClass(double a, double b, double c){
        this.a=a;
        this.b=b;
        this.c=c;
    }
    public double getArea(){
        double p=(a+b+c)/2;
        return Math.sqrt(p*(p-a)*(p-b)*(p-c));
    }
    public double getPerimeter(){
        return a+b+c;
    }
}