package W2;

import java.util.Scanner;

public class SuShuSister {
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        int i,j,x,y,pd,index=0;
        x = scanner.nextInt();
        y = scanner.nextInt();
        int[] sister = new int[100];
        for(i=x;i<y;i++)
        {
            pd = 1;
            for(j=2;j<i;j++)
            {
                if(i%j==0)
                {
                    pd=0;
                    break;
                }
            }
            if(pd == 1)
                sister[index++]=i;
        }
        for(i=0;i<index;i++)
        {
            if(sister[i]+2==sister[i+1])
                System.out.println(sister[i]+" "+sister[i+1]+" ");
        }
    }
}
