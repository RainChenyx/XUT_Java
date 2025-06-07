package W2;

import java.util.Scanner;

public class root
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        double a,b,c,delta,r,r1,r2;
        a = scanner.nextDouble();
        b = scanner.nextDouble();
        c = scanner.nextDouble();
        if(a!=0)
        {
            delta = b*b - 4*a*c;
            if(delta > 0) {
                r1 = (-b + Math.sqrt(delta)) / (2 * a);
                r2 = (-b - Math.sqrt(delta)) / (2 * a);
                System.out.print(String.format("%.2f",r1) + " " + String.format("%.2f",r2));
            }
            else if(delta == 0)
            {
                r = -b / (2 * a);
                System.out.printf("%.2f", r);
            }
            else if(delta < 0)
                System.out.print("no real root");
        }
        else if(a == 0)
        {
            System.out.printf("%.2f", -c/b);
        }
    }
}
