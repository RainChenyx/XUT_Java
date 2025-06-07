package W3;

import java.util.Scanner;

public class triangleTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double a = scanner.nextDouble();
        double b = scanner.nextDouble();
        double c = scanner.nextDouble();
        triangleClass triangle = new triangleClass(a,b,c);
        System.out.printf("area:%.2f\n",triangle.getArea());
        System.out.printf("circle:%.2f",triangle.getPerimeter());
    }
}