package W5;

import java.util.Arrays;
import java.util.Scanner;

public class string {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();

        String str1 = str.replace(" ", ",");
        System.out.println(str1);  // 1
        String[] numbers = str.split(" ");
        String str2 = Arrays.toString(numbers);
        System.out.println(str2);  // 2
        Arrays.sort(numbers);
        String str3 = Arrays.toString(numbers);
        System.out.println(str3);
    }
}