package W2;

import java.util.Scanner;

public class similar
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        double ex, x, step;
        x = scanner.nextDouble();
        step = scanner.nextDouble();
        for(double j = x; j <= 1.0; j += step)
        {
            ex = getEx(j);
            String Sj = String.format("%.2f",j);
            String Sex = String.format("%.6f",ex);
            System.out.println("x="+ Sj +"时,e(x)的值为:"+ Sex);
        }
        scanner.close();
    }
    public static double getEx(double x)
    {
        double i=1.0;
        double ex = 1.0, mid=1.0;
        while(Math.abs(mid=mid * (x/i)) >= 0.00001)
        {
            ex = ex+mid;
            i++;
        }
        return ex;
    }
}
