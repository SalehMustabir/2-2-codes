/*
====================================================================
QUESTION: ADAPTER + DECORATOR
====================================================================

A company has an existing payment processing system that works with
an old payment gateway.

The existing gateway has the following interface:

    class LegacyPaymentGateway {
        void makePayment(double amount);
    }

The new application expects every payment processor to implement:

    interface PaymentProcessor {
        void pay(double amount);
    }

The LegacyPaymentGateway cannot be modified.

The company also wants to provide optional additional security
features for payments:

    1. Encryption
       Adds encryption before payment processing.

    2. Fraud Detection
       Performs fraud checking before payment processing.

    3. Logging
       Records payment information.

These security features should be optional and can be combined
dynamically. New security features should be addable without
modifying the existing payment processor.

The system should support:

    - Existing legacy payment gateway
    - Adapter to make it compatible with the new system
    - Encryption
    - Fraud detection
    - Logging

Use appropriate Structural Design Patterns.

====================================================================
PATTERNS:

Adapter   -> Makes LegacyPaymentGateway compatible with
             PaymentProcessor.

Decorator -> Adds optional security features dynamically.

====================================================================
*/
import java.util.*;


// ================================================================
// 1. COMPONENT INTERFACE
// ================================================================
//
// This is the common interface expected by the new system.
//
// Decorators will also implement this interface.
// ================================================================

interface PaymentProcessor {

    void pay(double amount);
}


// ================================================================
// 2. LEGACY CLASS
// ================================================================
//
// This class already exists.
//
// IMPORTANT:
// We CANNOT modify it.
//
// Its method is:
//
//      makePayment()
//
// but our system expects:
//
//      pay()
//
// Therefore we need an ADAPTER.
// ================================================================

class LegacyPaymentGateway {

    public void makePayment(double amount) {

        System.out.println(
                "Legacy Gateway processing payment: $"
                        + amount
        );
    }
}


// ================================================================
// 3. ADAPTER
// ================================================================
//
// Converts:
//
//      LegacyPaymentGateway
//
// into:
//
//      PaymentProcessor
//
// ================================================================

class PaymentAdapter implements PaymentProcessor {

    private LegacyPaymentGateway gateway;

    public PaymentAdapter(
            LegacyPaymentGateway gateway) {

        this.gateway = gateway;
    }

    @Override
    public void pay(double amount) {

        // Translate the new interface into
        // the old interface.
        gateway.makePayment(amount);
    }
}


// ================================================================
// 4. DECORATOR INTERFACE
// ================================================================
//
// The decorator has the SAME interface as PaymentProcessor.
//
// This allows decorators to wrap another PaymentProcessor.
// ================================================================

interface PaymentDecorator extends PaymentProcessor {
}


// ================================================================
// 5. ENCRYPTION DECORATOR
// ================================================================

class EncryptionDecorator
        implements PaymentDecorator {

    private PaymentProcessor processor;

    public EncryptionDecorator(
            PaymentProcessor processor) {

        this.processor = processor;
    }

    @Override
    public void pay(double amount) {

        System.out.println(
                "Encrypting payment information..."
        );

        processor.pay(amount);
    }
}


// ================================================================
// 6. FRAUD DETECTION DECORATOR
// ================================================================

class FraudDetectionDecorator
        implements PaymentDecorator {

    private PaymentProcessor processor;

    public FraudDetectionDecorator(
            PaymentProcessor processor) {

        this.processor = processor;
    }

    @Override
    public void pay(double amount) {

        System.out.println(
                "Checking payment for fraud..."
        );

        processor.pay(amount);
    }
}


// ================================================================
// 7. LOGGING DECORATOR
// ================================================================

class LoggingDecorator
        implements PaymentDecorator {

    private PaymentProcessor processor;

    public LoggingDecorator(
            PaymentProcessor processor) {

        this.processor = processor;
    }

    @Override
    public void pay(double amount) {

        System.out.println(
                "Logging payment: $" + amount
        );

        processor.pay(amount);
    }
}


// ================================================================
// 8. CLIENT
// ================================================================

public class AdapterDecoratorDemo {

    public static void main(String[] args) {

        // ========================================================
        // Existing legacy gateway
        // ========================================================

        LegacyPaymentGateway legacyGateway =
                new LegacyPaymentGateway();


        // ========================================================
        // ADAPTER
        //
        // LegacyPaymentGateway is now compatible with
        // PaymentProcessor.
        // ========================================================

        PaymentProcessor processor =
                new PaymentAdapter(
                        legacyGateway
                );


        // ========================================================
        // DECORATORS
        //
        // Dynamically add optional features.
        // ========================================================

        processor =
                new EncryptionDecorator(
                        processor
                );

        processor =
                new FraudDetectionDecorator(
                        processor
                );

        processor =
                new LoggingDecorator(
                        processor
                );


        // ========================================================
        // Execute
        // ========================================================

        processor.pay(500);
    }
}