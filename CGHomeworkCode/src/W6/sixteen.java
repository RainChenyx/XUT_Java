package W6;

import java.util.Scanner;

public class sixteen {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        try {
            int n = Integer.parseInt(input,16);
            System.out.println(n);
        }
        catch (Exception e)
        {
            System.out.println("非十六进制字符");
        }
    }
}
