package W4;

import java.util.Scanner;

public class Complex {
    public double x;
    public double y;

    public Complex(double real, double imaginary) {
        x = real;
        y = imaginary;
    }

    public String toString() {
        return String.format("(%.2f,%.2f)", x, y);
    }

    public double getX() {
        return x;
    }

    // 获取复数的虚部
    public double getY() {
        return y;
    }
    // 静态方法 两复数相乘
    public static Complex multiply(Complex a, Complex b) {
        Complex result = new Complex(0, 0);  // 初始化临时复数result
        result.x = a.x * b.x - a.y * b.y;
        result.y = a.y * b.x + a.x * b.y;
        return result;
    }

    public Complex multiply(double a, double b) {
        Complex result = new Complex(0, 0);  // 初始化临时复数result
        result.x = x * a - y * b;
        result.y = y * a + x * b;
        return result;
    }

    // 两复数相乘
    public Complex multiply(Complex a) {
        Complex result = new Complex(0, 0);  // 初始化临时复数result
        double ax = a.getX();
        double ay = a.getY();
        result.x = x * ax - y * ay;
        result.y = x * ay + ax * y;
        return result;
    }

    // 复数求模
    public double modulus(){
        return modulus(x, y);
    }

    // 静态复数求模
    public static double modulus(double a, double b) {
        double result;
        result = a*a + b*b;
        return result;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double x,y;
        x = scanner.nextDouble();
        y = scanner.nextDouble();
        Complex a = new Complex(x, y);
        x = scanner.nextDouble();
        y = scanner.nextDouble();
        Complex b = new Complex(x, y);
        Complex c = Complex.multiply(a, b);
        System.out.println(c);
        Complex d = a.multiply(b);
        System.out.println(d);
        Complex e = b.multiply(a);
        System.out.println(e);
        System.out.printf("%.2f\n", a.modulus());
        System.out.printf("%.2f\n", b.modulus());
    }
}