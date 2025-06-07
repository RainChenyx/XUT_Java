package W4;

import java.util.Scanner;

class Point{
    public int x;
    public int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public double distance(Point p)  //求点到p点间距离
    {
        return Math.sqrt((this.x-p.x)*(this.x-p.x)+(this.y-p.y)*(this.y-p.y));
    }

    public double distance(int x,int y)  //求点到(x,y)点间距离
    {
        return Math.sqrt((this.x-x)*(this.x-x)+(this.y-y)*(this.y-y));
    }

    public static double distance(Point x,Point y)  //求点x到点y间距离
    {
        return Math.sqrt((x.x-y.x)*(x.x-y.x)+(x.y-y.y)*(x.y-y.y));
    }
}

public class Distance
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        int x1,y1,x2,y2;
        x1 = scanner.nextInt();
        y1 = scanner.nextInt();
        x2 = scanner.nextInt();
        y2 = scanner.nextInt();
        Point point1 = new Point(x1,y1);
        Point point2 = new Point(x2,y2);
        double d1 = point1.distance(point2);
        System.out.printf("%.2f\n",d1);
        double d2 = point1.distance(x2,y2);
        System.out.printf("%.2f\n",d2);
        double d3 = Point.distance(point1,point2);
        System.out.printf("%.2f\n",d3);
    }
}

