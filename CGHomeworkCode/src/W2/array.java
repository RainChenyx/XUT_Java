package W2;

import java.util.Scanner;

public class array {
    public static void main(String[] args)
    {
        int number,max;
        double sum=0, avg;
        Scanner scanner = new Scanner(System.in);
        number = scanner.nextInt();
        int[] arr = new int[number];
        for(int i = 0; i < number; i++)
        {
            arr[i] = scanner.nextInt();
        }
        max = arr[0];
        for(int i = 0; i < number; i++)
        {
            sum += arr[i];
            if (arr[i] > max)
                max = arr[i];
        }
        avg = sum / number;
        System.out.println("max:"+max+" \navg:"+avg);
        scanner.close();
    }
}
