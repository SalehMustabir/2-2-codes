// ============================================================
// 1. COMPONENT - Gift Item
// ============================================================

interface GiftItem {
    String getDescription();
    double getCost();
}


// ============================================================
// 2. CONCRETE COMPONENTS
// ============================================================

class DecorativeVase implements GiftItem {

    @Override
    public String getDescription() {
        return "Decorative Vase";
    }

    @Override
    public double getCost() {
        return 40.00;
    }
}


class WoodenSouvenir implements GiftItem {

    @Override
    public String getDescription() {
        return "Wooden Souvenir";
    }

    @Override
    public double getCost() {
        return 60.00;
    }
}


class CrystalShowpiece implements GiftItem {

    @Override
    public String getDescription() {
        return "Crystal Showpiece";
    }

    @Override
    public double getCost() {
        return 150.00;
    }
}


// ============================================================
// 3. DECORATOR - Optional Gift Wrapping
// ============================================================

interface GiftDecorator extends GiftItem {
    // Same interface as GiftItem
}


class GiftWrapping implements GiftDecorator {

    private GiftItem gift;

    public GiftWrapping(GiftItem gift) {
        this.gift = gift;
    }

    @Override
    public String getDescription() {
        return gift.getDescription() + " + Gift Wrapping";
    }

    @Override
    public double getCost() {
        return gift.getCost() + 2.00;
    }
}


// ============================================================
// 4. BRIDGE IMPLEMENTATION - Delivery Mode
// ============================================================

interface DeliveryMode {

    double getExtraCharge();

    String getDeliveryTime(String region);
}


// ------------------------------------------------------------
// Concrete Delivery Modes
// ------------------------------------------------------------

class NormalDelivery implements DeliveryMode {

    @Override
    public double getExtraCharge() {
        return 0.00;
    }

    @Override
    public String getDeliveryTime(String region) {

        if (region.equals("Local"))
            return "1 week";

        if (region.equals("National"))
            return "1–2 weeks";

        if (region.equals("International"))
            return "2–3 weeks";

        return "Unknown";
    }
}


class ExpressDelivery implements DeliveryMode {

    @Override
    public double getExtraCharge() {
        return 10.00;
    }

    @Override
    public String getDeliveryTime(String region) {

        if (region.equals("International"))
            return "1 week";

        return "2 days";
    }
}


class PriorityDelivery implements DeliveryMode {

    @Override
    public double getExtraCharge() {
        return 25.00;
    }

    @Override
    public String getDeliveryTime(String region) {

        if (region.equals("International"))
            return "5 days";

        return "1 day";
    }
}


// ============================================================
// 5. BRIDGE ABSTRACTION - Delivery Region
// ============================================================

interface DeliveryRegion {

    double getDeliveryCharge(double miles);

    String getRegionName();

    String getDeliveryTime();
}


// ============================================================
// 6. REFINED ABSTRACTIONS
// ============================================================

class LocalDelivery implements DeliveryRegion {

    private DeliveryMode mode;

    public LocalDelivery(DeliveryMode mode) {
        this.mode = mode;
    }

    @Override
    public double getDeliveryCharge(double miles) {
        return (miles * 1.00) + mode.getExtraCharge();
    }

    @Override
    public String getRegionName() {
        return "Local";
    }

    @Override
    public String getDeliveryTime() {
        return mode.getDeliveryTime("Local");
    }
}


class NationalDelivery implements DeliveryRegion {

    private DeliveryMode mode;

    public NationalDelivery(DeliveryMode mode) {
        this.mode = mode;
    }

    @Override
    public double getDeliveryCharge(double miles) {
        return (miles * 1.00) + 20.00 + mode.getExtraCharge();
    }

    @Override
    public String getRegionName() {
        return "National";
    }

    @Override
    public String getDeliveryTime() {
        return mode.getDeliveryTime("National");
    }
}


class InternationalDelivery implements DeliveryRegion {

    private DeliveryMode mode;

    public InternationalDelivery(DeliveryMode mode) {
        this.mode = mode;
    }

    @Override
    public double getDeliveryCharge(double miles) {
        return 500.00 + mode.getExtraCharge();
    }

    @Override
    public String getRegionName() {
        return "International";
    }

    @Override
    public String getDeliveryTime() {
        return mode.getDeliveryTime("International");
    }
}


// ============================================================
// 7. MAIN / CLIENT
// ============================================================

public class A1 {

    public static void main(String[] args) {

        // ====================================================
        // CASE 1
        // Vase $40
        // Local, 10 miles
        // Gift wrapping
        // ====================================================

        GiftItem vase = new DecorativeVase();

        vase = new GiftWrapping(vase);

        DeliveryRegion local =
                new LocalDelivery(
                        new NormalDelivery()
                );

        double total1 =
                vase.getCost()
                + local.getDeliveryCharge(10);

        System.out.println("CASE 1");
        System.out.println("----------------------------");
        System.out.println("Item: " + vase.getDescription());
        System.out.println("Total Cost: $" + total1);
        System.out.println("Estimated Delivery: "
                + local.getDeliveryTime());


        // ====================================================
        // CASE 2
        // Wooden Souvenir $60
        // National, 50 miles
        // Gift wrapping
        // Express
        // ====================================================

        GiftItem souvenir = new WoodenSouvenir();

        souvenir = new GiftWrapping(souvenir);

        DeliveryRegion national =
                new NationalDelivery(
                        new ExpressDelivery()
                );

        double total2 =
                souvenir.getCost()
                + national.getDeliveryCharge(50);

        System.out.println("\nCASE 2");
        System.out.println("----------------------------");
        System.out.println("Item: " + souvenir.getDescription());
        System.out.println("Total Cost: $" + total2);
        System.out.println("Estimated Delivery: "
                + national.getDeliveryTime());


        // ====================================================
        // CASE 3
        // Crystal Showpiece $150
        // International
        // Priority
        // ====================================================

        GiftItem showpiece = new CrystalShowpiece();

        DeliveryRegion international =
                new InternationalDelivery(
                        new PriorityDelivery()
                );

        double total3 =
                showpiece.getCost()
                + international.getDeliveryCharge(0);

        System.out.println("\nCASE 3");
        System.out.println("----------------------------");
        System.out.println("Item: " + showpiece.getDescription());
        System.out.println("Total Cost: $" + total3);
        System.out.println("Estimated Delivery: "
                + international.getDeliveryTime());
    }
}