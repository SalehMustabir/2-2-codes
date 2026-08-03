interface PaymentStrategy {

    void pay(double amount);

}

class CreditCardPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Paid $" + amount + " using Credit Card");

    }
}

class PayPalPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Paid $" + amount + " using PayPal");

    }
}

class UpiPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Paid $" + amount + " using UPI");

    }
}

class PaymentService {

    private PaymentStrategy paymentStrategy;

    public PaymentService(PaymentStrategy paymentStrategy) {

        this.paymentStrategy = paymentStrategy;

    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {

        this.paymentStrategy = paymentStrategy;

    }

    public void checkout(double amount) {

        paymentStrategy.pay(amount);

    }
}

public class Strategy {

    public static void main(String[] args) {

        PaymentService paymentService =
                new PaymentService(new CreditCardPayment());

        paymentService.checkout(1000);

        System.out.println();

        paymentService.setPaymentStrategy(new PayPalPayment());

        paymentService.checkout(500);

        System.out.println();

        paymentService.setPaymentStrategy(new UpiPayment());

        paymentService.checkout(250);

    }
}