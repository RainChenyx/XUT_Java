package OOP1B;

public class Food {
    private final String name;
    private final double price;
    public int sell_count;  // 销量

    Food(String name, double price, int sell_count){
        this.name = name;
        this.price = price;
        this.sell_count = sell_count;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    @Override
    public String toString(){
        return "菜品: " + name + " 价格: " + price + " 销量: " + sell_count;
    }
}
