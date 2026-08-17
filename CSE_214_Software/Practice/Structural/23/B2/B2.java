import java.util.ArrayList;
import java.util.List;


// ============================================================
// 1. BASE COMPONENT
// Everything in the order can do these things.
// ============================================================

interface OrderItem {

    double getPrice();

    void print(String indent);
}


// ============================================================
// 2. LEAF - FOOD
// ============================================================

class Food implements OrderItem {

    private String name;
    private double price;

    public Food(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void print(String indent) {

        System.out.printf(
                "%sFood: %s (£%.2f)%n",
                indent,
                name,
                price
        );

        System.out.println();
    }
}


// ============================================================
// 3. LEAF - GROCERY
// ============================================================

class Grocery implements OrderItem {

    private String name;
    private double price;

    public Grocery(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void print(String indent) {

        System.out.printf(
                "%sGrocery: %s (£%.2f)%n",
                indent,
                name,
                price
        );

        System.out.println();
    }
}


// ============================================================
// 4. SET MENU - COMPOSITE
//
// A Set Menu can contain FOOD ONLY.
// It cannot contain another SetMenu or Grocery.
// Price = 90% of total individual food prices.
// ============================================================

class SetMenu implements OrderItem {

    private String name;

    private List<Food> foods = new ArrayList<>();

    public SetMenu(String name) {
        this.name = name;
    }

    // Only Food can be added
    public void addFood(Food food) {
        foods.add(food);
    }

    public void removeFood(Food food) {
        foods.remove(food);
    }

    public Food getFood(int index) {
        return foods.get(index);
    }

    @Override
    public double getPrice() {

        double total = 0;

        for (Food food : foods) {
            total += food.getPrice();
        }

        // 10% discount
        return total * 0.90;
    }

    @Override
    public void print(String indent) {

        System.out.println(
                indent + "Set Menu: " + name
        );

        for (Food food : foods) {
            food.print(indent + "   ");
        }

        System.out.println();
    }
}


// ============================================================
// 5. GROCERY PACKAGE - COMPOSITE
//
// A Grocery Package can contain:
// - Grocery items
// - Other Grocery Packages
//
// It cannot contain SetMenu or Food.
// ============================================================

class GroceryPackage implements OrderItem {

    private String name;

    private List<OrderItem> items = new ArrayList<>();

    public GroceryPackage(String name) {
        this.name = name;
    }

    // Add Grocery items
    public void add(Grocery grocery) {
        items.add(grocery);
    }

    // Add another Grocery Package
    public void add(GroceryPackage pack) {
        items.add(pack);
    }

    public void remove(OrderItem item) {
        items.remove(item);
    }

    public OrderItem getChild(int index) {
        return items.get(index);
    }

    @Override
    public double getPrice() {

        double total = 0;

        for (OrderItem item : items) {
            total += item.getPrice();
        }

        return total;
    }

    @Override
    public void print(String indent) {

        System.out.println(
                indent + "Package: " + name
        );

        for (OrderItem item : items) {
            item.print(indent + "   ");
        }

        System.out.println();
    }
}


// ============================================================
// 6. ORDER
// Provided class
// ============================================================

class Order {

    private List<OrderItem> items = new ArrayList<>();

    public void add(OrderItem item) {
        items.add(item);
    }

    public double getTotalPrice() {

        double total = 0;

        for (OrderItem item : items) {
            total += item.getPrice();
        }

        return total;
    }

    public void printReceipt() {

        System.out.println("========== RECEIPT ==========");

        for (OrderItem item : items) {
            item.print("");
        }

        System.out.println("-----------------------------");

        System.out.printf(
                "Total Bill: £%.2f%n",
                getTotalPrice()
        );
    }
}


// ============================================================
// 7. MAIN
// ============================================================

public class B2 {

    public static void main(String[] args) {

        // ====================================================
        // Foods
        // ====================================================

        Food burger =
                new Food("Burger", 8);

        Food pizza =
                new Food("Pizza", 10);

        Food fries =
                new Food("French Fries", 3);


        // ====================================================
        // Set Menu
        // Burger £8 + Fries £3 = £11
        // 10% discount = £9.90
        // ====================================================

        SetMenu lunch =
                new SetMenu("Lunch Combo");

        lunch.addFood(burger);
        lunch.addFood(fries);


        // ====================================================
        // Grocery Items
        // ====================================================

        Grocery rice =
                new Grocery("Rice", 20);

        Grocery oil =
                new Grocery("Cooking Oil", 12);

        Grocery eggs =
                new Grocery("Eggs", 6);

        Grocery sugar =
                new Grocery("Sugar", 5);


        // ====================================================
        // Small Grocery Package
        // ====================================================

        GroceryPackage breakfastPack =
                new GroceryPackage("Breakfast Pack");

        breakfastPack.add(eggs);
        breakfastPack.add(sugar);


        // ====================================================
        // Large Grocery Package
        // Contains another package
        // ====================================================

        GroceryPackage monthlyPack =
                new GroceryPackage("Monthly Essentials");

        monthlyPack.add(rice);
        monthlyPack.add(oil);
        monthlyPack.add(breakfastPack);


        // ====================================================
        // Customer Order
        // ====================================================

        Order order = new Order();

        order.add(pizza);
        order.add(lunch);
        order.add(rice);
        order.add(monthlyPack);


        // ====================================================
        // Print
        // ====================================================

        order.printReceipt();
    }
}