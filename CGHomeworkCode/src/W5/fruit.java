package W5;

import java.util.Scanner;

// 抽象类 Fruit
abstract class Fruit {
    public double weight;

    public Fruit(double weight) {
        this.weight = weight;
    }

    abstract void getWeight();
}

// 苹果类，继承自 Fruit
class Apple extends Fruit {
    public String name = "Apple";

    public Apple(double weight) {
        super(weight);
    }

    @Override
    public void getWeight() {
        System.out.printf("%s:%.1f\n", name, super.weight);
    }
}

// 桃子类，继承自 Fruit
class Peach extends Fruit {
    public String name = "Peach";

    public Peach(double weight) {
        super(weight);
    }

    @Override
    public void getWeight() {
        System.out.printf("%s:%.1f\n", name, super.weight);
    }
}

// 橘子类，继承自 Fruit
class Orange extends Fruit {
    public String name = "Orange";

    public Orange(double weight) {
        super(weight);
    }

    @Override
    public void getWeight() {
        System.out.printf("%s:%.1f\n", name, super.weight);
    }
}

public class fruit {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // 读取输入的水果重量
        double appleWeight = scanner.nextDouble();
        double peachWeight = scanner.nextDouble();
        double orangeWeight = scanner.nextDouble();

        // 创建水果对象数组
        Fruit[] fruits = new Fruit[3];
        fruits[0] = new Apple(appleWeight);
        fruits[1] = new Peach(peachWeight);
        fruits[2] = new Orange(orangeWeight);

        // 遍历数组，输出每种水果的类型和重量
        for (Fruit fruit : fruits) {
            fruit.getWeight();
        }

        scanner.close();
    }
}