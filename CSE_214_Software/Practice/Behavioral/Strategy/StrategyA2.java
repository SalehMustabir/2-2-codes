import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================
 * SMART DISCOUNT CALCULATION SYSTEM
 * Design Pattern: Strategy Pattern
 * ============================================================
 *
 * There are three independent discount policies:
 *
 * 1. Purchase Amount Discount
 * 2. Customer Category Discount
 * 3. Payment Method Discount
 *
 * Each policy calculates its own discount.
 * The system selects ONLY the highest discount.
 *
 * Discounts are NOT combined.
 */


/*
 * ------------------------------------------------------------
 * CUSTOMER CATEGORY
 * ------------------------------------------------------------
 */
enum CustomerCategory {
    REGULAR,
    PREMIUM
}


/*
 * ------------------------------------------------------------
 * PAYMENT METHOD
 * ------------------------------------------------------------
 */
enum PaymentMethod {
    CARD,
    MFS,
    CASH
}


/*
 * ------------------------------------------------------------
 * DISCOUNT STRATEGY INTERFACE
 * ------------------------------------------------------------
 *
 * This is the Strategy interface.
 *
 * Every discount policy must implement calculateDiscount()
 * and return a discount percentage.
 */
interface DiscountStrategy {

    // Returns discount percentage.
    double calculateDiscount(double purchaseAmount);

    // Returns the name of the discount policy.
    String getPolicyName();
}


/*
 * ------------------------------------------------------------
 * PURCHASE AMOUNT DISCOUNT STRATEGY
 * ------------------------------------------------------------
 *
 * Rules:
 *
 * Below 1000       -> 0%
 * 1000 - 1999      -> 5%
 * 2000 - 2999      -> 10%
 * 3000 - 3999      -> 15%
 * 4000 - 4999      -> 20%
 * 5000 or more     -> 25%
 */
class PurchaseAmountDiscount implements DiscountStrategy {

    @Override
    public double calculateDiscount(double purchaseAmount) {

        if (purchaseAmount < 1000) {
            return 0;
        }
        else if (purchaseAmount < 2000) {
            return 5;
        }
        else if (purchaseAmount < 3000) {
            return 10;
        }
        else if (purchaseAmount < 4000) {
            return 15;
        }
        else if (purchaseAmount < 5000) {
            return 20;
        }
        else {
            return 25;
        }
    }

    @Override
    public String getPolicyName() {
        return "Purchase Amount Discount";
    }
}


/*
 * ------------------------------------------------------------
 * CUSTOMER CATEGORY DISCOUNT STRATEGY
 * ------------------------------------------------------------
 *
 * REGULAR  -> 5%
 * PREMIUM  -> 15%
 */
class CustomerCategoryDiscount implements DiscountStrategy {

    private CustomerCategory category;

    public CustomerCategoryDiscount(CustomerCategory category) {
        this.category = category;
    }

    @Override
    public double calculateDiscount(double purchaseAmount) {

        if (category == CustomerCategory.PREMIUM) {
            return 15;
        }

        return 5;
    }

    @Override
    public String getPolicyName() {
        return "Customer Category Discount";
    }
}


/*
 * ------------------------------------------------------------
 * PAYMENT METHOD DISCOUNT STRATEGY
 * ------------------------------------------------------------
 *
 * CARD -> 2%
 * MFS  -> 5%
 * CASH -> 8%
 */
class PaymentMethodDiscount implements DiscountStrategy {

    private PaymentMethod paymentMethod;

    public PaymentMethodDiscount(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public double calculateDiscount(double purchaseAmount) {

        switch (paymentMethod) {

            case CARD:
                return 2;

            case MFS:
                return 5;

            case CASH:
                return 8;

            default:
                return 0;
        }
    }

    @Override
    public String getPolicyName() {
        return "Payment Method Discount";
    }
}


/*
 * ------------------------------------------------------------
 * DISCOUNT RESULT
 * ------------------------------------------------------------
 *
 * This class stores:
 *
 * - Policy name
 * - Discount percentage
 *
 * It is useful for displaying which policy was selected.
 */
class DiscountResult {

    private String policyName;
    private double discountPercentage;

    public DiscountResult(
            String policyName,
            double discountPercentage) {

        this.policyName = policyName;
        this.discountPercentage = discountPercentage;
    }

    public String getPolicyName() {
        return policyName;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}


/*
 * ------------------------------------------------------------
 * DISCOUNT CALCULATOR
 * ------------------------------------------------------------
 *
 * This is the CONTEXT of the Strategy Pattern.
 *
 * It does not contain the actual discount rules.
 * Instead, it receives different strategies and evaluates them.
 */
class DiscountCalculator {

    private List<DiscountStrategy> strategies;

    public DiscountCalculator() {

        // List stores all applicable discount strategies.
        strategies = new ArrayList<>();
    }


    /*
     * Add a discount strategy.
     */
    public void addStrategy(DiscountStrategy strategy) {
        strategies.add(strategy);
    }


    /*
     * Evaluate all strategies and return the strategy
     * that provides the highest discount.
     *
     * IMPORTANT:
     * Discounts are NOT added together.
     */
    public DiscountResult findBestDiscount(double purchaseAmount) {

        double highestDiscount = -1;
        String selectedPolicy = "";

        // Evaluate every discount strategy independently.
        for (DiscountStrategy strategy : strategies) {

            double discount =
                    strategy.calculateDiscount(purchaseAmount);

            System.out.println(
                    strategy.getPolicyName()
                    + " -> "
                    + discount
                    + "%"
            );

            /*
             * If this discount is higher than the current
             * highest discount, select this policy.
             *
             * If two policies have the same discount,
             * the first one is kept.
             * The problem allows any one of the tied policies.
             */
            if (discount > highestDiscount) {

                highestDiscount = discount;
                selectedPolicy = strategy.getPolicyName();
            }
        }

        return new DiscountResult(
                selectedPolicy,
                highestDiscount
        );
    }
}


/*
 * ------------------------------------------------------------
 * MAIN CLASS
 * ------------------------------------------------------------
 */
public class StrategyA2 {

    public static void main(String[] args) {

        /*
         * ====================================================
         * EXAMPLE 1
         * ====================================================
         *
         * Purchase Amount : ৳3,500
         * Customer Type   : PREMIUM
         * Payment Method  : CASH
         *
         * Purchase Amount Discount -> 15%
         * Customer Category Discount -> 15%
         * Payment Method Discount -> 8%
         *
         * Highest = 15%
         *
         * Only 15% is applied.
         */

        double purchaseAmount = 3500;

        CustomerCategory customerCategory =
                CustomerCategory.PREMIUM;

        PaymentMethod paymentMethod =
                PaymentMethod.CASH;


        /*
         * Create the Context.
         */
        DiscountCalculator calculator =
                new DiscountCalculator();


        /*
         * Add all applicable strategies.
         *
         * Each strategy works independently.
         */
        calculator.addStrategy(
                new PurchaseAmountDiscount()
        );

        calculator.addStrategy(
                new CustomerCategoryDiscount(
                        customerCategory
                )
        );

        calculator.addStrategy(
                new PaymentMethodDiscount(
                        paymentMethod
                )
        );


        System.out.println(
                "======================================"
        );

        System.out.println(
                "SMART DISCOUNT CALCULATION"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Purchase Amount : ৳" + purchaseAmount
        );

        System.out.println(
                "Customer Type   : " + customerCategory
        );

        System.out.println(
                "Payment Method  : " + paymentMethod
        );

        System.out.println(
                "\nAvailable Discounts:"
        );


        /*
         * Find the highest discount.
         */
        DiscountResult result =
                calculator.findBestDiscount(purchaseAmount);


        /*
         * Calculate the actual discount amount.
         */
        double discountAmount =
                purchaseAmount
                * result.getDiscountPercentage()
                / 100;


        /*
         * Calculate final payable amount.
         */
        double finalAmount =
                purchaseAmount - discountAmount;


        System.out.println(
                "\n======================================"
        );

        System.out.println(
                "FINAL RESULT"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Selected Policy : "
                + result.getPolicyName()
        );

        System.out.println(
                "Applied Discount: "
                + result.getDiscountPercentage()
                + "%"
        );

        System.out.println(
                "Discount Amount : ৳"
                + discountAmount
        );

        System.out.println(
                "Final Payable   : ৳"
                + finalAmount
        );
    }
}

