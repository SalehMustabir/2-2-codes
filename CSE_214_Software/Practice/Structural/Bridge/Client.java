// ==========================================
// 1. DECORATOR PATTERN (Gift Items & Add-ons)
// ==========================================

interface GiftItem {
    String getDescription();
    double getPrice();
}

class BaseGiftItem implements GiftItem {
    private String description;
    private double basePrice;

    public BaseGiftItem(String desc, double price) {
        this.description = desc;
        this.basePrice = price;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getPrice() {
        return basePrice;
    }
}

abstract class GiftDecorator implements GiftItem {
    protected GiftItem decoratedItem;

    public GiftDecorator(GiftItem item) {
        this.decoratedItem = item;
    }

    @Override
    public String getDescription() {
        return decoratedItem.getDescription();
    }

    @Override
    public double getPrice() {
        return decoratedItem.getPrice();
    }
}

class GiftWrapping extends GiftDecorator {
    public GiftWrapping(GiftItem item) {
        super(item);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Gift Wrapping";
    }

    @Override
    public double getPrice() {
        return super.getPrice() + 2.0; // Adds $2 wrapping charge
    }
}

// ==========================================
// 2. BRIDGE PATTERN - IMPLEMENTATION LAYER (Delivery Modes)
// ==========================================

interface DeliveryMode {
    double getAdditionalCost();
    String getLocalTime();
    String getNationalTime();
    String getInternationalTime();
}

class StandardDelivery implements DeliveryMode {
    @Override
    public double getAdditionalCost() {
        return 0.0;
    }

    @Override
    public String getLocalTime() {
        return "1 week";
    }

    @Override
    public String getNationalTime() {
        return "1-2 weeks";
    }

    @Override
    public String getInternationalTime() {
        return "2-3 weeks";
    }
}

class ExpressDelivery implements DeliveryMode {
    @Override
    public double getAdditionalCost() {
        return 10.0; // Adds $10
    }

    @Override
    public String getLocalTime() {
        return "2 days";
    }

    @Override
    public String getNationalTime() {
        return "2 days";
    }

    @Override
    public String getInternationalTime() {
        return "1 week";
    }
}

class PriorityDelivery implements DeliveryMode {
    @Override
    public double getAdditionalCost() {
        return 25.0; // Adds $25
    }

    @Override
    public String getLocalTime() {
        return "1 day";
    }

    @Override
    public String getNationalTime() {
        return "1 day";
    }

    @Override
    public String getInternationalTime() {
        return "5 days";
    }
}

// ==========================================
// 3. BRIDGE PATTERN - ABSTRACTION LAYER (Delivery Regions)
// ==========================================

abstract class DeliveryRegion {
    protected DeliveryMode deliveryMode;

    public DeliveryRegion(DeliveryMode mode) {
        this.deliveryMode = mode;
    }

    public abstract double getBaseCost();

    public double getDeliveryCost() {
        return getBaseCost() + deliveryMode.getAdditionalCost();
    }

    public abstract String getEstimatedDeliveryTime();
}

class LocalDelivery extends DeliveryRegion {
    private int distanceMiles;

    public LocalDelivery(int dist, DeliveryMode mode) {
        super(mode);
        this.distanceMiles = dist;
    }

    @Override
    public double getBaseCost() {
        return distanceMiles * 1.0; // $1 per mile
    }

    @Override
    public String getEstimatedDeliveryTime() {
        return deliveryMode.getLocalTime();
    }
}

class NationalDelivery extends DeliveryRegion {
    private int distanceMiles;

    public NationalDelivery(int dist, DeliveryMode mode) {
        super(mode);
        this.distanceMiles = dist;
    }

    @Override
    public double getBaseCost() {
        return (distanceMiles * 1.0) + 20.0; // $1 per mile + $20 fixed surcharge
    }

    @Override
    public String getEstimatedDeliveryTime() {
        return deliveryMode.getNationalTime();
    }
}

class InternationalDelivery extends DeliveryRegion {
    public InternationalDelivery(DeliveryMode mode) {
        super(mode);
    }

    @Override
    public double getBaseCost() {
        return 500.0; // $500 fixed surcharge
    }

    @Override
    public String getEstimatedDeliveryTime() {
        return deliveryMode.getInternationalTime();
    }
}

// ==========================================
// 4. CLIENT
// ==========================================

public class Client {
    private static void printOrderSummary(String caseLabel, GiftItem item, DeliveryRegion delivery) {
        double totalCost = item.getPrice() + delivery.getDeliveryCost();
        System.out.println("=== " + caseLabel + " ===");
        System.out.println("Item: " + item.getDescription());
        System.out.printf("Total Cost: $%.0f%n", totalCost);
        System.out.println("Estimated Delivery Time: " + delivery.getEstimatedDeliveryTime());
        System.out.println();
    }

    public static void main(String[] args) {
        // Case 1: Decorative vase ($40), 10 miles local delivery, gift wrapping, standard delivery
        GiftItem item1 = new GiftWrapping(new BaseGiftItem("Decorative Vase", 40.0));
        DeliveryRegion delivery1 = new LocalDelivery(10, new StandardDelivery());
        printOrderSummary("Case 1", item1, delivery1); // Total: $52, Time: 1 week

        // Case 2: Wooden souvenir ($60), 50 miles national delivery, gift wrapping, Express Delivery
        GiftItem item2 = new GiftWrapping(new BaseGiftItem("Wooden Souvenir", 60.0));
        DeliveryRegion delivery2 = new NationalDelivery(50, new ExpressDelivery());
        printOrderSummary("Case 2", item2, delivery2); // Total: $142, Time: 2 days

        // Case 3: Crystal showpiece ($150), international destination, Priority Delivery
        GiftItem item3 = new BaseGiftItem("Crystal Showpiece", 150.0);
        DeliveryRegion delivery3 = new InternationalDelivery(new PriorityDelivery());
        printOrderSummary("Case 3", item3, delivery3); // Total: $675, Time: 5 days
    }
}