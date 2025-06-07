package W6;

import java.util.Scanner;

public class lottery {
    public static void main(String[] args) {
        int prize = 7;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try
            {
                String input = scanner.nextLine();
                if (input.equals("quit"))
                {
                    System.out.println("结束");
                    break;
                }
                else if (input.equals("give me hint"))
                {
                    System.out.println(prize);
                    break;
                }
                else
                {
                    int guess = Integer.parseInt(input);
                    if (guess == prize) {
                        System.out.println("你中奖了");
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("非法输入，请输入整数");
            }
        }
        scanner.close();
    }
}
