// Behavioural Design Pattern
// Duration: 30 Minutes

// Problem: Online Order Processing System

// An e-commerce company processes different types of customer orders.
// Every order follows a common processing workflow, but some steps are
// different depending on the type of order.

// The system supports two types of orders:

// 1. Online Order
// 2. Store Pickup Order

// Every order follows these steps:

// 1. Validate the order.
// 2. Process payment.
// 3. Prepare the order.
// 4. Deliver or hand over the order.
// 5. Send confirmation.

// For an Online Order:
// - The order is validated.
// - Payment is processed online.
// - The product is packed.
// - The product is delivered to the customer's address.
// - A delivery confirmation is sent.

// For a Store Pickup Order:
// - The order is validated.
// - Payment is processed.
// - The product is prepared for pickup.
// - The product is kept at the selected store.
// - A pickup confirmation is sent.

// The overall workflow must remain the same for every order type.
// However, individual steps may have different implementations.

// Requirements:

// 1. Use the Template Method Design Pattern.
// 2. The complete order-processing algorithm should be defined in
//    one place.
// 3. Common steps should be implemented in the parent class.
// 4. Steps that vary between order types should be implemented by
//    subclasses.
// 5. The client should not be able to change the order of the workflow.
// 6. It should be easy to add another order type later with minimal
//    modification to existing code.

// Required operations:

// - processOrder()
// - validateOrder()
// - processPayment()
// - prepareOrder()
// - deliverOrder()
// - sendConfirmation()

// Write a Java program to implement this system.

// ============================================================
// TEMPLATE METHOD DESIGN PATTERN
// Online Order Processing System
// ============================================================


// ============================================================
// ABSTRACT CLASS
// ============================================================

// This is the Abstract Class in Template Method Pattern.
//
// It defines the common workflow for processing an order.
//
// The order of steps is fixed inside processOrder().
//
// Subclasses will provide their own implementation
// for the steps that are different.
abstract class OrderProcessor {


    // ========================================================
    // TEMPLATE METHOD
    // ========================================================

    // This method defines the complete algorithm.
    //
    // The client cannot change the order of these steps.
    //
    // This is called the Template Method.

    public final void processOrder() {

        // Step 1
        validateOrder();

        // Step 2
        processPayment();

        // Step 3
        prepareOrder();

        // Step 4
        deliverOrder();

        // Step 5
        sendConfirmation();

        System.out.println("Order processing completed.\n");
    }


    // ========================================================
    // COMMON OPERATION
    // ========================================================

    // This operation is common for all order types.
    // Therefore, it is implemented directly in the
    // abstract parent class.

    protected void validateOrder() {

        System.out.println("Order validation successful.");
    }


    // ========================================================
    // ABSTRACT OPERATIONS
    // ========================================================

    // These methods are different for different order types.
    //
    // Subclasses MUST implement them.

    protected abstract void processPayment();

    protected abstract void prepareOrder();

    protected abstract void deliverOrder();

    protected abstract void sendConfirmation();
}


// ============================================================
// ONLINE ORDER
// ============================================================

// Concrete Class 1
//
// Provides the implementation of the variable steps
// for an Online Order.

class OnlineOrder extends OrderProcessor {


    @Override
    protected void processPayment() {

        System.out.println(
            "Processing payment through online payment gateway."
        );
    }


    @Override
    protected void prepareOrder() {

        System.out.println(
            "Packing the product for delivery."
        );
    }


    @Override
    protected void deliverOrder() {

        System.out.println(
            "Delivering the product to customer's address."
        );
    }


    @Override
    protected void sendConfirmation() {

        System.out.println(
            "Sending delivery confirmation to customer."
        );
    }
}


// ============================================================
// STORE PICKUP ORDER
// ============================================================

// Concrete Class 2
//
// Provides a different implementation of the variable steps
// for a Store Pickup Order.

class StorePickupOrder extends OrderProcessor {


    @Override
    protected void processPayment() {

        System.out.println(
            "Processing payment for store pickup."
        );
    }


    @Override
    protected void prepareOrder() {

        System.out.println(
            "Preparing the product for customer pickup."
        );
    }


    @Override
    protected void deliverOrder() {

        System.out.println(
            "Keeping the product at the selected store."
        );
    }


    @Override
    protected void sendConfirmation() {

        System.out.println(
            "Sending pickup confirmation to customer."
        );
    }
}


// ============================================================
// MAIN CLASS
// ============================================================

public class Template {

    public static void main(String[] args) {


        // ====================================================
        // ONLINE ORDER
        // ====================================================

        System.out.println(
            "===== ONLINE ORDER ====="
        );

        // Reference type is the abstract class.
        // Actual object is OnlineOrder.

        OrderProcessor onlineOrder =
                new OnlineOrder();

        // The same template method is called.
        //
        // The workflow will always be:
        //
        // validate
        //     ->
        // payment
        //     ->
        // preparation
        //     ->
        // delivery
        //     ->
        // confirmation

        onlineOrder.processOrder();


        // ====================================================
        // STORE PICKUP ORDER
        // ====================================================

        System.out.println(
            "===== STORE PICKUP ORDER ====="
        );

        OrderProcessor pickupOrder =
                new StorePickupOrder();

        // Same processOrder() method is used.
        //
        // But the subclass implementations of the
        // variable steps are executed.

        pickupOrder.processOrder();
    }
}

