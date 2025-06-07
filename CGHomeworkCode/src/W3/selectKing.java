package W3;

import java.util.Scanner;

public class selectKing {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n,k,m;
        n = scanner.nextInt();
        k = scanner.nextInt();
        m = scanner.nextInt();
        selectKingClass selectKing = new selectKingClass(n,k,m);
        selectKing.king();
    }
}
