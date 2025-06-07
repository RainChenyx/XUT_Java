package W2;

import java.util.Scanner;

public class find {
    public  static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        double[] Arr = new double[number];
        for(int i = 0; i < number; i++)
        {
            Arr[i] = scanner.nextInt();
        }
        double target = scanner.nextDouble();
        int Index = -1;
        for(int i = 0; i < number; i++)
        {
            if(Arr[i]==target)
                Index = i;
        }
        if(Index!=-1)
            System.out.println("["+Index+"]");
        else
            System.out.println("target "+ target +" is not exist");
    }
}
