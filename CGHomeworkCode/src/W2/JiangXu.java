package W2;

import java.util.Scanner;

public class JiangXu {
    public static void main(String[] args)
    {
        int x,a,result=1;
        Scanner scanner = new Scanner(System.in);
        x = scanner.nextInt();
        a = x%10;
        while(x!=0)
        {
            if(a > x%10)
            {
                System.out.println("false");
                result = 0;
                break;
            }
            a = x%10;
            x = x/10;
        }
        if(result == 1)
            System.out.println("true");
    }
}
