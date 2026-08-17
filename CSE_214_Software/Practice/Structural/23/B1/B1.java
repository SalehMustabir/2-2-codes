import java.util.ArrayList;
import java.util.List;


// ============================================================
// 1. BASE COMPONENT
// Everything (Gift Items and Gift Packages) can do this.
// ============================================================

interface GiftComponent {

    String getName();

    String getDescription();

    double getCost();

    void print();
}


// ============================================================
// 2. COMPOSITE INTERFACE
// Only Gift Packages can add/remove children.
// ============================================================

interface CompositeGift extends GiftComponent {

    void add(GiftComponent component);

    void remove(GiftComponent component);

    GiftComponent getChild(int index);
}


// ============================================================
// 3. LEAF - Individual Gift Item
// ============================================================

class GiftItem implements GiftComponent {

    String name;
    String description;
    double price;

    public GiftItem(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getCost() {
        return price;
    }

    @Override
    public void print() {
        System.out.println(
                "  " + getName() + " - $" + getCost()
        );

        System.out.println(
                "     -- " + getDescription()
        );
    }
}


// ============================================================
// 4. COMPOSITE - Personal Gift Package
// ============================================================

class PersonalGiftPackage implements CompositeGift {

    String name;
    String creator;

    List<GiftComponent> components = new ArrayList<>();

    public PersonalGiftPackage(String name, String creator) {
        this.name = name;
        this.creator = creator;
    }

    @Override
    public void add(GiftComponent component) {
        components.add(component);
    }

    @Override
    public void remove(GiftComponent component) {
        components.remove(component);
    }

    @Override
    public GiftComponent getChild(int index) {
        return components.get(index);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Personal Gift Package by " + creator;
    }

    @Override
    public double getCost() {

        double total = 0;

        for (GiftComponent component : components) {
            total += component.getCost();
        }

        return total;
    }

    @Override
    public void print() {

        System.out.println(
                "\n" + getName()
                + " | Creator: " + creator
        );

        System.out.println(
                "Personal Gift Package"
        );

        System.out.println("---------------------");

        for (GiftComponent component : components) {
            component.print();
        }

        System.out.println(
                "Package Cost: $" + getCost()
        );
    }
}


// ============================================================
// 5. COMPOSITE - Corporate Gift Package
// ============================================================

class CorporateGiftPackage implements CompositeGift {

    String name;
    String creator;

    List<GiftComponent> components = new ArrayList<>();

    public CorporateGiftPackage(String name, String creator) {
        this.name = name;
        this.creator = creator;
    }

    @Override
    public void add(GiftComponent component) {
        components.add(component);
    }

    @Override
    public void remove(GiftComponent component) {
        components.remove(component);
    }

    @Override
    public GiftComponent getChild(int index) {
        return components.get(index);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Corporate Gift Package by " + creator;
    }

    @Override
    public double getCost() {

        double total = 0;

        for (GiftComponent component : components) {
            total += component.getCost();
        }

        return total;
    }

    @Override
    public void print() {

        System.out.println(
                "\n" + getName()
                + " | Creator: " + creator
        );

        System.out.println(
                "Corporate Gift Package"
        );

        System.out.println("---------------------");

        for (GiftComponent component : components) {
            component.print();
        }

        System.out.println(
                "Package Cost: $" + getCost()
        );
    }
}


// ============================================================
// 6. DECORATOR
// Packaging is an optional additional feature.
// ============================================================

interface GiftPackageDecorator extends GiftComponent {

    // Same basic interface as GiftComponent.
}


// ============================================================
// 7. PREMIUM GIFT BOX DECORATOR
// Adds $15
// ============================================================

class PremiumGiftBox implements GiftPackageDecorator {

    private GiftComponent giftPackage;

    public PremiumGiftBox(GiftComponent giftPackage) {
        this.giftPackage = giftPackage;
    }

    @Override
    public String getName() {
        return giftPackage.getName();
    }

    @Override
    public String getDescription() {
        return giftPackage.getDescription()
                + " + Premium Gift Box with Decorative Ribbon";
    }

    @Override
    public double getCost() {
        return giftPackage.getCost() + 15.00;
    }

    @Override
    public void print() {

        giftPackage.print();

        System.out.println(
                "Premium Gift Box: +$15"
        );

        System.out.println(
                "Final Cost: $" + getCost()
        );
    }
}


// ============================================================
// 8. ECO-FRIENDLY GIFT BOX DECORATOR
// Adds $8
// ============================================================

class EcoFriendlyGiftBox implements GiftPackageDecorator {

    private GiftComponent giftPackage;

    public EcoFriendlyGiftBox(GiftComponent giftPackage) {
        this.giftPackage = giftPackage;
    }

    @Override
    public String getName() {
        return giftPackage.getName();
    }

    @Override
    public String getDescription() {
        return giftPackage.getDescription()
                + " + Eco-Friendly Gift Box";
    }

    @Override
    public double getCost() {
        return giftPackage.getCost() + 8.00;
    }

    @Override
    public void print() {

        giftPackage.print();

        System.out.println(
                "Eco-Friendly Gift Box: +$8"
        );

        System.out.println(
                "Final Cost: $" + getCost()
        );
    }
}


// ============================================================
// 9. CLIENT
// ============================================================

public class B1 {

    public static void main(String[] args) {


        // ====================================================
        // Individual Gift Items
        // ====================================================

        GiftComponent chocolate =
                new GiftItem(
                        "Chocolate",
                        "Premium chocolate box",
                        20.00
                );

        GiftComponent mug =
                new GiftItem(
                        "Mug",
                        "Ceramic coffee mug",
                        10.00
                );

        GiftComponent perfume =
                new GiftItem(
                        "Perfume",
                        "Luxury perfume",
                        50.00
                );

        GiftComponent flowers =
                new GiftItem(
                        "Flowers",
                        "Fresh flower bouquet",
                        25.00
                );


        // ====================================================
        // PERSONAL PACKAGE
        // ====================================================

        CompositeGift personalPackage =
                new PersonalGiftPackage(
                        "Eid Special",
                        "Rahim"
                );

        personalPackage.add(chocolate);
        personalPackage.add(mug);

        // Add packaging using Decorator

        GiftComponent premiumPersonalPackage =
                new PremiumGiftBox(personalPackage);


        System.out.println("\n==============================");
        System.out.println("PERSONAL PACKAGE");
        System.out.println("==============================");

        premiumPersonalPackage.print();


        // ====================================================
        // CORPORATE PACKAGE
        // ====================================================

        CompositeGift corporatePackage =
                new CorporateGiftPackage(
                        "Corporate Eid Package",
                        "Karim"
                );

        corporatePackage.add(perfume);
        corporatePackage.add(flowers);

        // Add Eco-Friendly packaging

        GiftComponent ecoCorporatePackage =
                new EcoFriendlyGiftBox(corporatePackage);


        System.out.println("\n==============================");
        System.out.println("CORPORATE PACKAGE");
        System.out.println("==============================");

        ecoCorporatePackage.print();


        // ====================================================
        // COMPOSITE INSIDE COMPOSITE
        //
        // A customer can use an existing package
        // created by the company or another customer.
        // ====================================================

        CompositeGift anotherPackage =
                new PersonalGiftPackage(
                        "Mixed Eid Package",
                        "Sakib"
                );

        anotherPackage.add(
                new GiftItem(
                        "Book",
                        "Inspirational book",
                        30.00
                )
        );

        anotherPackage.add(
                new GiftItem(
                        "Flowers",
                        "Rose bouquet",
                        20.00
                )
        );


        // Add an existing package to another package

        anotherPackage.add(
                premiumPersonalPackage
        );


        System.out.println("\n==============================");
        System.out.println("PACKAGE CONTAINING PACKAGE");
        System.out.println("==============================");

        anotherPackage.print();


        // ====================================================
        // POLYMORPHISM
        // Everything can be treated as GiftComponent
        // ====================================================

        GiftComponent root = anotherPackage;

        System.out.println("\n==============================");
        System.out.println("TOTAL");
        System.out.println("==============================");

        System.out.println(
                "Package: " + root.getName()
        );

        System.out.println(
                "Total Cost: $" + root.getCost()
        );
    }
}