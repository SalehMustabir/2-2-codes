/*
====================================================================
QUESTION: ADAPTER + COMPOSITE
====================================================================

An e-commerce company sells products individually and also allows
customers to create bundles.

A Product has:
    - name
    - price

A Bundle can contain multiple products and other bundles.

The Order class expects every purchased object to implement:

    interface OrderItem {
        double getPrice();
        void print(String indent);
    }

However, the company has an existing legacy product system whose
products are represented using the following incompatible class:

    class LegacyProduct {
        String getProductName();
        double getProductPrice();
    }

The LegacyProduct class cannot be modified.

The system should:

1. Allow legacy products to be added to an order.
2. Allow individual products to be added.
3. Allow bundles to contain individual products and other bundles.
4. Allow the Order to treat individual products and bundles uniformly.
5. Display the complete order hierarchically.
6. Calculate the total price.
7. Support adding new product types and bundle types in the future.

Use appropriate Structural Design Patterns.

====================================================================
PATTERNS:
    Adapter  -> LegacyProduct -> OrderItem
    Composite -> Product + Bundle hierarchy
====================================================================
*/

import java.util.ArrayList;
import java.util.List;

// ================================================================
// 1. COMPONENT INTERFACE
// ================================================================
//
// Everything that can appear in an Order follows OrderItem.
//
// Both:
//
//      Product
//      Bundle
//      Adapted LegacyProduct
//
// can therefore be treated uniformly.
//
// This is the BASE of the COMPOSITE pattern.
// ================================================================

// ================================================================
// 1. COMPONENT INTERFACE
// ================================================================
//
// Everything that can appear in an Order follows OrderItem.
//
// Both:
//
//      Product
//      Bundle
//      Adapted LegacyProduct
//
// can therefore be treated uniformly.
//
// This is the BASE of the COMPOSITE pattern.
// ================================================================
import java.util.ArrayList;
import java.util.List;

interface OrderItem{

    double getPrice();

    void print(String indent);
}


// ================================================================
// 2. LEAF - NORMAL PRODUCT
// ================================================================

class Product implements OrderItem {

    private String name;
    private double price;

    public Product(String name, double price) {
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
                "%sProduct: %s (£%.2f)%n",
                indent,
                name,
                price
        );
    }
}


// ================================================================
// 3. LEGACY PRODUCT
// ================================================================
//
// This class comes from an old system.
//
// We CANNOT modify it.
//
// Notice that it does NOT implement OrderItem.
//
// Therefore:
//
//     order.add(legacyProduct)
//
// would NOT compile.
//
// We need an ADAPTER.
// ================================================================

class LegacyProduct {

    private String productName;
    private double productPrice;

    public LegacyProduct(
            String productName,
            double productPrice) {

        this.productName = productName;
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }
}


// ================================================================
// 4. ADAPTER
// ================================================================
//
// Converts LegacyProduct into an OrderItem.
//
// The client/order does not need to know that the product
// originally came from a legacy system.
//
//
//       LegacyProduct
//            |
//            | ADAPTER
//            v
//        OrderItem
//
// ================================================================

class LegacyProductAdapter implements OrderItem {

    private LegacyProduct legacyProduct;

    public LegacyProductAdapter(
            LegacyProduct legacyProduct) {

        this.legacyProduct = legacyProduct;
    }

    @Override
    public double getPrice() {

        return legacyProduct.getProductPrice();
    }

    @Override
    public void print(String indent) {

        System.out.printf(
                "%sLegacy Product: %s (£%.2f)%n",
                indent,
                legacyProduct.getProductName(),
                legacyProduct.getProductPrice()
        );
    }
}


// ================================================================
// 5. COMPOSITE INTERFACE
// ================================================================
//
// Only objects that can contain children need add/remove.
//
// We keep these operations separate from OrderItem so that a
// Product cannot accidentally call:
//
//     product.add(...)
//
// This follows the style of your Composite template.
// ================================================================

interface CompositeOrderItem extends OrderItem {

    void add(OrderItem item);

    void remove(OrderItem item);

    OrderItem getChild(int index);
}


// ================================================================
// 6. BUNDLE - COMPOSITE
// ================================================================
//
// A Bundle can contain:
//
//     Product
//     LegacyProductAdapter
//     Other Bundle
//
// because all of them are OrderItem.
//
// This is the main COMPOSITE.
// ================================================================

class Bundle implements CompositeOrderItem {

    private String name;

    private List<OrderItem> items =
            new ArrayList<>();

    public Bundle(String name) {
        this.name = name;
    }

    @Override
    public void add(OrderItem item) {

        items.add(item);
    }

    @Override
    public void remove(OrderItem item) {

        items.remove(item);
    }

    @Override
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
                indent + "Bundle: " + name
        );

        for (OrderItem item : items) {

            item.print(indent + "   ");
        }
    }
}


// ================================================================
// 7. ORDER
// ================================================================
//
// The Order class is deliberately SIMPLE.
//
// It doesn't care whether an OrderItem is:
//
//     Product
//     Bundle
//     LegacyProductAdapter
//
// Everything is treated uniformly.
//
// This is the major benefit of COMPOSITE + ADAPTER.
// ================================================================

class Order {

    private List<OrderItem> items =
            new ArrayList<>();

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

        System.out.println(
                "========== ORDER =========="
        );

        for (OrderItem item : items) {

            item.print("");
        }

        System.out.println(
                "---------------------------"
        );

        System.out.printf(
                "Total: £%.2f%n",
                getTotalPrice()
        );
    }
}


// ================================================================
// 8. CLIENT
// ================================================================

public class AdapterCompositeDemo {

    public static void main(String[] args) {


        // ========================================================
        // NORMAL PRODUCTS
        // ========================================================

        Product burger =
                new Product(
                        "Burger",
                        8.00
                );

        Product pizza =
                new Product(
                        "Pizza",
                        10.00
                );

        Product fries =
                new Product(
                        "Fries",
                        3.00
                );


        // ========================================================
        // LEGACY PRODUCT
        // ========================================================

        LegacyProduct oldDrink =
                new LegacyProduct(
                        "Legacy Cola",
                        2.50
                );


        // ========================================================
        // ADAPTER
        //
        // LegacyProduct itself is NOT an OrderItem.
        //
        // Adapter makes it an OrderItem.
        // ========================================================

        OrderItem cola =
                new LegacyProductAdapter(
                        oldDrink
                );


        // ========================================================
        // COMPOSITE
        //
        // Create a meal bundle.
        // ========================================================

        CompositeOrderItem meal =
                new Bundle(
                        "Meal Bundle"
                );

        meal.add(burger);
        meal.add(fries);
        meal.add(cola);


        // ========================================================
        // NESTED COMPOSITE
        //
        // Another bundle can contain the previous bundle.
        // ========================================================

        CompositeOrderItem familyBundle =
                new Bundle(
                        "Family Bundle"
                );

        familyBundle.add(pizza);
        familyBundle.add(meal);


        // ========================================================
        // ORDER
        //
        // Notice how simple this remains.
        //
        // It only knows about OrderItem.
        // ========================================================

        Order order =
                new Order();

        order.add(pizza);
        order.add(cola);
        order.add(familyBundle);


        // ========================================================
        // OUTPUT
        // ========================================================

        order.printReceipt();
    }
}