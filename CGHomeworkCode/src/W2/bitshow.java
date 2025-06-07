package W2;

import java.util.Scanner;

public class bitshow {
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        int x = scanner.nextInt();
        scanner.close();
        String binaryStr = Integer.toBinaryString(x);
        System.out.println(binaryStr);
    }
}
