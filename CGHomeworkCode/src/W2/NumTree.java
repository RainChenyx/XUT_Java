package W2;

import java.util.Scanner;

public class NumTree {
    public static void main(String[] args)
    {
        int i,n,j,k;
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        for(i=1;i<=n;i++)
        {
            for(j=n-i;j>0;j--)
            {
                System.out.print(" ");
            }
            for(k=0;k <2*i-1;k++)
            {
                System.out.print(i);
            }
            System.out.println();
        }
    }
}
