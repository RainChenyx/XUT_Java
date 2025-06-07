package W5;

import java.util.Scanner;

public class sentence {
    public static void main(String[] args) {
        int number =0;
        double ave;
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        str = str.replace(",", "");
        str = str.replace(".", "");
        str = str.replace("?", "");
        String[] word = str.split(" ");
        for (int i = 0; i < word.length; i++)
        {
            System.out.println(word[i]);
            number += word[i].length();
        }
        ave = (double)number/word.length;
        System.out.printf("%.4f",ave);
    }
}