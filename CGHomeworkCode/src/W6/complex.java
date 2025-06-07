package W6;

import java.util.Scanner;

class ComplexNumber {
    double real;
    double imaginary;

    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public static ComplexNumber parseComplex(String input) {
        if (input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')')
        {
            input = input.substring(1, input.length() - 1);
            String[] parts = input.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("数据格式错");
            }
            double real = Double.parseDouble(parts[0]);
            if (parts[1].charAt(parts[1].length()-1) != 'i') {
                throw new IllegalArgumentException("数据格式错");
            }
            parts[1] = parts[1].substring(0, parts[1].length() - 1);
            double imaginary = Double.parseDouble(parts[1]);
            return new ComplexNumber(real, imaginary);
        }
        throw new IllegalArgumentException("数据格式错");
    }

    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(this.real + other.real, this.imaginary + other.imaginary);
    }

    @Override
    public String toString() {
        return "(" + real + "," + imaginary + "i)";
    }
    public void Print(){
        System.out.printf("(%.1f,%.1fi)", real,imaginary);
    }
}

public class complex {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        try {
            String[] parts = input.split("\\+");
            if (parts.length != 2) {
                throw new IllegalArgumentException("数据格式错");
            }
            ComplexNumber num1 = ComplexNumber.parseComplex(parts[0]);
            ComplexNumber num2 = ComplexNumber.parseComplex(parts[1]);
            ComplexNumber sum = num1.add(num2);
            sum.Print();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        scanner.close();
    }
}    