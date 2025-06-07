package W3;

import java.util.Scanner;

public class date {
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        int year = scanner.nextInt();
        int month = scanner.nextInt();
        int day = scanner.nextInt();
        dateClass dateClass = new dateClass(year, month, day);
        dateClass.Print();
    }
}