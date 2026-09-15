// ---------------------------------------------------------
// Strategy Interface
// ---------------------------------------------------------
// Every discount policy will implement this interface.
// Each strategy independently calculates a discount percentage.
interface DiscountStrategy {

    // Returns the discount percentage for a purchase.
    double calculateDiscount(double amount,
                             String customerType,
                             String paymentMethod);
}


// ---------------------------------------------------------
// Concrete Strategy 1: Purchase Amount Discount
// ---------------------------------------------------------
class PurchaseAmountDiscount implements DiscountStrategy {

    @Override
    public double calculateDiscount(double amount,
                                    String customerType,
                                    String paymentMethod) {

        // For every complete Tk 1000:
        // 1000 -> 5%
        // 2000 -> 10%
        // 3000 -> 15%
        // ...
        // Maximum discount is 25%.

        int completeThousands = (int) (amount / 1000);

        // Each complete Tk 1000 gives 5%.
        double discount = completeThousands * 5;

        // Maximum purchase amount discount is 25%.
        if (discount > 25) {
            discount = 25;
        }

        return discount;
    }
}


// ---------------------------------------------------------
// Concrete Strategy 2: Customer Category Discount
// ---------------------------------------------------------
class CustomerCategoryDiscount implements DiscountStrategy {

    @Override
    public double calculateDiscount(double amount,
                                    String customerType,
                                    String paymentMethod) {

        if (customerType.equalsIgnoreCase("PREMIUM")) {
            return 15;
        }

        // REGULAR customer gets 5%.
        if (customerType.equalsIgnoreCase("REGULAR")) {
            return 5;
        }

        // If category is unknown, no discount.
        return 0;
    }
}


// ---------------------------------------------------------
// Concrete Strategy 3: Payment Method Discount
// ---------------------------------------------------------
class PaymentMethodDiscount implements DiscountStrategy {

    @Override
    public double calculateDiscount(double amount,
                                    String customerType,
                                    String paymentMethod) {

        if (paymentMethod.equalsIgnoreCase("CARD")) {
            return 2;
        }

        if (paymentMethod.equalsIgnoreCase("MFS")) {
            return 5;
        }

        if (paymentMethod.equalsIgnoreCase("CASH")) {
            return 8;
        }

        // Unknown payment method -> no discount.
        return 0;
    }
}


// ---------------------------------------------------------
// Context
// ---------------------------------------------------------
// The DiscountCalculator acts as the Context.
// It receives different discount strategies and evaluates them.
class DiscountCalculator {

    private DiscountStrategy[] strategies;

    public DiscountCalculator(DiscountStrategy[] strategies) {
        this.strategies = strategies;
    }

    // Evaluates ALL discount policies and returns the highest one.
    public double getBestDiscount(double amount,
                                  String customerType,
                                  String paymentMethod) {

        double highestDiscount = 0;

        // Evaluate every strategy independently.
        for (DiscountStrategy strategy : strategies) {

            double currentDiscount =
                    strategy.calculateDiscount(
                            amount,
                            customerType,
                            paymentMethod
                    );

            // Keep only the highest discount.
            if (currentDiscount > highestDiscount) {
                highestDiscount = currentDiscount;
            }
        }

        return highestDiscount;
    }
}


// ---------------------------------------------------------
// Main Class
// ---------------------------------------------------------
public class StrategyA2 {

    public static void main(String[] args) {

        // Create all available discount strategies.
        DiscountStrategy[] strategies = {

                new PurchaseAmountDiscount(),
                new CustomerCategoryDiscount(),
                new PaymentMethodDiscount()
        };

        // Create the Context.
        DiscountCalculator calculator =
                new DiscountCalculator(strategies);


        // -------------------------------------------------
        // Example 1
        // -------------------------------------------------
        double amount = 3500;
        String customerType = "PREMIUM";
        String paymentMethod = "CASH";

        double bestDiscount =
                calculator.getBestDiscount(
                        amount,
                        customerType,
                        paymentMethod
                );

        double payableAmount =
                amount - (amount * bestDiscount / 100);

        System.out.println("Purchase Amount: Tk " + amount);
        System.out.println("Customer Type: " + customerType);
        System.out.println("Payment Method: " + paymentMethod);

        System.out.println("Applied Discount: "
                + bestDiscount + "%");

        System.out.println("Final Payable Amount: Tk "
                + payableAmount);


        // -------------------------------------------------
        // Example 2
        // -------------------------------------------------
        amount = 5500;
        customerType = "PREMIUM";
        paymentMethod = "CASH";

        bestDiscount =
                calculator.getBestDiscount(
                        amount,
                        customerType,
                        paymentMethod
                );

        payableAmount =
                amount - (amount * bestDiscount / 100);

        System.out.println("\n-------------------------");

        System.out.println("Purchase Amount: Tk " + amount);
        System.out.println("Customer Type: " + customerType);
        System.out.println("Payment Method: " + paymentMethod);

        System.out.println("Applied Discount: "
                + bestDiscount + "%");

        System.out.println("Final Payable Amount: Tk "
                + payableAmount);
    }
}