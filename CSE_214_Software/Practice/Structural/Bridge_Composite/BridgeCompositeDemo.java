/*
====================================================================
QUESTION: BRIDGE + COMPOSITE
====================================================================

A company sells products and product bundles.

An individual Product has a name and price.

A Bundle can contain:
    - individual Products
    - other Bundles

Therefore, Products and Bundles should be treated uniformly.

The company also supports different pricing strategies:

    1. Regular Pricing
       No discount.

    2. Discount Pricing
       10% discount.

    3. Premium Pricing
       20% additional charge.

A bundle can contain other bundles, and different bundles may use
different pricing strategies.

The system should:

1. Display the complete hierarchy.
2. Calculate the total price recursively.
3. Support nested bundles.
4. Allow pricing strategies to change independently.
5. Allow new pricing strategies and new product/bundle types
   without significantly modifying existing code.

Use appropriate Structural Design Patterns.

====================================================================
PATTERNS USED:

    COMPOSITE
    ---------
    Product + Bundle are treated uniformly.
    Bundle can contain Product and other Bundle objects.

    BRIDGE
    ------
    Bundle has-a PricingStrategy.
    Pricing strategy can vary independently from the bundle hierarchy.

====================================================================
*/


import java.util.ArrayList;
import java.util.List;


// ================================================================
// 1. BRIDGE - IMPLEMENTATION INTERFACE
// ================================================================
//
// This is the implementation side of the Bridge.
//
// Different pricing algorithms will implement this interface.
//
// The important Bridge relationship is:
//
//      Bundle
//         |
//         | HAS-A
//         v
//   PricingStrategy
//
// ================================================================

interface PricingStrategy {

    double calculatePrice(double basePrice);
}


// ================================================================
// 2. CONCRETE IMPLEMENTATION - REGULAR PRICING
// ================================================================

class RegularPricing implements PricingStrategy {

    @Override
    public double calculatePrice(double basePrice) {

        return basePrice;
    }
}


// ================================================================
// 3. CONCRETE IMPLEMENTATION - DISCOUNT PRICING
// ================================================================

class DiscountPricing implements PricingStrategy {

    @Override
    public double calculatePrice(double basePrice) {

        // 10% discount
        return basePrice * 0.90;
    }
}


// ================================================================
// 4. CONCRETE IMPLEMENTATION - PREMIUM PRICING
// ================================================================

class PremiumPricing implements PricingStrategy {

    @Override
    public double calculatePrice(double basePrice) {

        // 20% additional charge
        return basePrice * 1.20;
    }
}


// ================================================================
// 5. COMPOSITE - BASE COMPONENT
// ================================================================
//
// Both Product and Bundle implement ProductComponent.
//
// Therefore the client can treat:
//
//      Product
//      Bundle
//
// in exactly the same way.
//
// ================================================================

interface ProductComponent {

    String getName();

    double getPrice();

    void print(String indent);
}


// ================================================================
// 6. COMPOSITE - LEAF
// ================================================================
//
// Product is a LEAF.
//
// It cannot contain other ProductComponents.
//
// ================================================================

class Product implements ProductComponent {

    private String name;
    private double price;

    public Product(String name, double price) {

        this.name = name;
        this.price = price;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public double getPrice() {

        return price;
    }

    @Override
    public void print(String indent) {

        System.out.printf(
                "%sProduct: %s ($%.2f)%n",
                indent,
                name,
                price
        );
    }
}


// ================================================================
// 7. COMPOSITE + BRIDGE
// ================================================================
//
// Bundle is the COMPOSITE because:
//
//      Bundle contains ProductComponent objects.
//
// Bundle is also the ABSTRACTION side of the BRIDGE because:
//
//      Bundle HAS-A PricingStrategy.
//
// The two relationships are:
//
// COMPOSITE:
//      Bundle
//        |
//        v
//      List<ProductComponent>
//
// BRIDGE:
//      Bundle
//        |
//        v
//      PricingStrategy
//
// ================================================================

class Bundle implements ProductComponent {

    private String name;


    // ============================================================
    // COMPOSITE PART
    // ============================================================
    //
    // This allows a Bundle to contain:
    //
    //      Product
    //      Bundle
    //
    // because both implement ProductComponent.
    //
    // ============================================================

    private List<ProductComponent> children =
            new ArrayList<>();


    // ============================================================
    // BRIDGE PART
    // ============================================================
    //
    // THIS IS THE IMPORTANT BRIDGE REFERENCE.
    //
    // One interface instance is stored inside another class.
    //
    // Bundle does not know whether the strategy is:
    //
    //      RegularPricing
    //      DiscountPricing
    //      PremiumPricing
    //
    // ============================================================

    private PricingStrategy pricingStrategy;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public Bundle(
            String name,
            PricingStrategy pricingStrategy) {

        this.name = name;

        // Bridge is established here.
        this.pricingStrategy = pricingStrategy;
    }


    // ============================================================
    // COMPOSITE OPERATION
    // ============================================================

    public void add(ProductComponent component) {

        children.add(component);
    }


    // ============================================================
    // COMPOSITE OPERATION
    // ============================================================

    public void remove(ProductComponent component) {

        children.remove(component);
    }


    // ============================================================
    // COMPOSITE OPERATION
    // ============================================================

    public ProductComponent getChild(int index) {

        return children.get(index);
    }


    // ============================================================
    // BRIDGE OPERATION
    // ============================================================
    //
    // First, Composite calculates the price of all children.
    //
    // Then Bridge delegates the pricing algorithm to the
    // PricingStrategy object.
    //
    // Bundle itself does NOT implement:
    //
    //      discount
    //      premium
    //      regular
    //
    // It simply delegates.
    //
    // ============================================================

    @Override
    public double getPrice() {

        double basePrice = 0;


        // -------------------------------
        // COMPOSITE
        // -------------------------------
        //
        // Recursively calculate children.
        //
        // If a child is another Bundle,
        // its getPrice() is called recursively.
        //

        for (ProductComponent component : children) {

            basePrice += component.getPrice();
        }


        // -------------------------------
        // BRIDGE
        // -------------------------------
        //
        // Delegate the actual pricing
        // calculation to the implementation.
        //

        return pricingStrategy.calculatePrice(
                basePrice
        );
    }


    @Override
    public String getName() {

        return name;
    }


    // ============================================================
    // PRINT HIERARCHY
    // ============================================================

    @Override
    public void print(String indent) {

        System.out.println(
                indent + "Bundle: " + name
        );


        // Recursively print children.
        for (ProductComponent component : children) {

            component.print(
                    indent + "   "
            );
        }


        // Display the calculated bundle price.
        System.out.printf(
                "%sBundle Price: $%.2f%n",
                indent,
                getPrice()
        );

        System.out.println();
    }
}


// ================================================================
// 8. CLIENT
// ================================================================

public class BridgeCompositeDemo {

    public static void main(String[] args) {


        // ========================================================
        // INDIVIDUAL PRODUCTS
        // ========================================================

        Product laptop =
                new Product(
                        "Laptop",
                        1000
                );

        Product phone =
                new Product(
                        "Phone",
                        500
                );

        Product mouse =
                new Product(
                        "Mouse",
                        50
                );

        Product keyboard =
                new Product(
                        "Keyboard",
                        100
                );


        // ========================================================
        // NESTED BUNDLE
        // ========================================================
        //
        // Accessories Bundle uses DiscountPricing.
        //
        // This demonstrates BOTH:
        //
        // Composite:
        //     Bundle contains Products.
        //
        // Bridge:
        //     Bundle uses DiscountPricing.
        //
        // ========================================================

        Bundle accessories =
                new Bundle(
                        "Accessories Bundle",
                        new DiscountPricing()
                );

        accessories.add(mouse);
        accessories.add(keyboard);


        // ========================================================
        // MAIN BUNDLE
        // ========================================================
        //
        // Electronics Bundle uses PremiumPricing.
        //
        // It contains:
        //
        //      Laptop
        //      Phone
        //      Accessories Bundle
        //
        // Therefore we have a nested Composite tree.
        //
        // ========================================================

        Bundle electronics =
                new Bundle(
                        "Electronics Bundle",
                        new PremiumPricing()
                );

        electronics.add(laptop);
        electronics.add(phone);
        electronics.add(accessories);


        // ========================================================
        // CLIENT TREATS EVERYTHING AS ProductComponent
        // ========================================================
        //
        // This is the main benefit of Composite.
        //
        // The client does not care whether root is:
        //
        //      Product
        //      Bundle
        //
        // ========================================================

        ProductComponent root = electronics;


        // ========================================================
        // PRINT COMPLETE HIERARCHY
        // ========================================================

        System.out.println(
                "========== PRODUCTS =========="
        );

        root.print("");


        // ========================================================
        // TOTAL PRICE
        // ========================================================

        System.out.println(
                "=============================="
        );

        System.out.printf(
                "Total Price: $%.2f%n",
                root.getPrice()
        );
    }
}