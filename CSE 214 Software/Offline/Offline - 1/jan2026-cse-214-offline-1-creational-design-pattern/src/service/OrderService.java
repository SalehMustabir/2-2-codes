package service;

import model.DeliveryType;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.PaymentMethod;
import model.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates order creation.
 *
 * Several methods below repeat long Order constructor calls with many optional
 * parameters. That is intentional assignment material for refactoring.
 */
public class OrderService {
    private int nextNumber = 1001;

    public OrderItem createOrderItem(MenuItem item, int quantity, Size size, boolean extraCheese, boolean spicy, String note) {
        OrderItem.Builder builder = new OrderItem.Builder(item, quantity);
        builder.size(size);
        builder.extraCheese(extraCheese);
        builder.spicy(spicy);
        builder.note(note);
        return builder.build();
        //return new OrderItem(item, quantity, size, extraCheese, spicy, note);
    }

    public Order createDeliveryOrder(String customerName,
                                     String phone,
                                     String address,
                                     List<OrderItem> items,
                                     String couponCode,
                                     boolean rushOrder,
                                     String specialInstructions) {
        // return new Order(nextOrderId(), customerName, phone,
        //         DeliveryType.DELIVERY,
        //         address,
        //         PaymentMethod.CASH,
        //         null,
        //         couponCode,
        //         false,
        //         true,
        //         0,
        //         rushOrder,
        //         items,
        //         specialInstructions);

        // public Order(String orderId,
        //          String customerName,
        //          String phone,
        //          DeliveryType deliveryType,
        //          String deliveryAddress,
        //          PaymentMethod paymentMethod,
        //          LocalDateTime scheduledTime,
        //          String couponCode,
        //          boolean giftWrap,
        //          boolean cutleryRequired,
        //          int loyaltyPointsToRedeem,
        //          boolean rushOrder,
        //          List<OrderItem> items,
        //          String specialInstructions)
        Order.Builder builder = new Order.Builder(nextOrderId(), customerName, phone, items);
        builder.deliveryType(DeliveryType.DELIVERY);
        //builder.paymentMethod(PaymentMethod.CASH);
        builder.deliveryAddress(address);
        // builder.paymentMethod(null);
        builder.couponCode(couponCode);
        builder.rushOrder(rushOrder);
        builder.specialInstructions(specialInstructions);
        //builder.giftWrap(false);
        //builder.cutleryRequired(true);
        builder.loyaltyPointsToRedeem(0);
        //builder.scheduledTime(null);




        return builder.build();
    }

    public Order createPickupOrder(String customerName, String phone, List<OrderItem> items) {
        // return new Order(nextOrderId(), customerName, phone,
        //         DeliveryType.PICKUP,
        //         "",
        //         PaymentMethod.CASH,
        //         null,
        //         "",
        //         false,
        //         true,
        //         0,
        //         false,
        //         items,
        //         "");

        // public Order(String orderId,
        //          String customerName,
        //          String phone,
        //          DeliveryType deliveryType,
        //          String deliveryAddress,
        //          PaymentMethod paymentMethod,
        //          LocalDateTime scheduledTime,
        //          String couponCode,
        //          boolean giftWrap,
        //          boolean cutleryRequired,
        //          int loyaltyPointsToRedeem,
        //          boolean rushOrder,
        //          List<OrderItem> items,
        //          String specialInstructions)
        Order.Builder builder = new Order.Builder(nextOrderId() ,customerName, phone, items);
        builder.deliveryType(DeliveryType.PICKUP);
        //builder.paymentMethod(PaymentMethod.CASH);
        //builder.scheduledTime(null);
        //builder.couponCode("");
        //builder.giftWrap(false);
        //builder.cutleryRequired(true);
        //builder.loyaltyPointsToRedeem(0);
        builder.rushOrder(false);


        return builder.build();
    }

    public Order createScheduledGiftOrder(String customerName,
                                          String phone,
                                          String address,
                                          List<OrderItem> items,
                                          LocalDateTime scheduledTime) {
                                            
        // return new Order(nextOrderId(), customerName, phone,
        //         DeliveryType.DELIVERY,
        //         address,
        //         PaymentMethod.CARD,
        //         scheduledTime,
        //         "WELCOME10",
        //         true,
        //         false,
        //         25,
        //         false,
        //         items,
        //         "Please call before delivery");
        // public Order(String orderId,
        //          String customerName,
        //          String phone,
        //          DeliveryType deliveryType,
        //          String deliveryAddress,
        //          PaymentMethod paymentMethod,
        //          LocalDateTime scheduledTime,
        //          String couponCode,
        //          boolean giftWrap,
        //          boolean cutleryRequired,
        //          int loyaltyPointsToRedeem,
        //          boolean rushOrder,
        //          List<OrderItem> items,
        //          String specialInstructions)
        
        Order.Builder builder = new Order.Builder(nextOrderId(), customerName, phone, items);
        builder.deliveryType(DeliveryType.DELIVERY);
        builder.deliveryAddress(address);
        builder.paymentMethod(PaymentMethod.CARD);
        builder.scheduledTime(scheduledTime);
        builder.couponCode("WELCOME10");
        builder.giftWrap(true);
        builder.cutleryRequired(false);
        builder.loyaltyPointsToRedeem(25);
        builder.rushOrder(false);
        builder.specialInstructions("Please call before delivery");

        return builder.build();
    }

    public Order createSampleFamilyOrder(MenuCatalog catalog) {
        List<OrderItem> items = new ArrayList<>();

items.add(new OrderItem.Builder(catalog.findByCode("P01"), 2)
        .size(Size.LARGE)
        .extraCheese(true)
        .note("half spicy")
        .build());

items.add(new OrderItem.Builder(catalog.findByCode("B02"), 3)
        
        .extraCheese(true)
        .spicy(true)
        .build());

items.add(new OrderItem.Builder(catalog.findByCode("D02"), 4)
        
        .note("less sugar")
        .build());

items.add(new OrderItem.Builder(catalog.findByCode("S02"), 2)
        .size(Size.LARGE)
        .spicy(true)
        
        .build());

        return new Order.Builder(nextOrderId(), "Sample Family", "01711111111", items)
        .deliveryType(DeliveryType.DELIVERY)
        .deliveryAddress("House 25, Road 4, Dhanmondi")
        .paymentMethod(PaymentMethod.MOBILE_BANKING)
        .couponCode("FAMILY15")
        .loyaltyPointsToRedeem(50)
        .rushOrder(true)
        .specialInstructions("Deliver together")
        .build();
    }

    private String nextOrderId() {
        return "FF-" + nextNumber++;
    }
}

