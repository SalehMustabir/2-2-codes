package model;

import java.util.Objects;

/**
 * Represents a customized menu item inside an order.
 */
public class OrderItem {
    public static final double EXTRA_CHEESE_PRICE = 60.0;

    private final MenuItem menuItem;
    private final int quantity;
    private final Size size;
    private final boolean extraCheese;    
    private final boolean spicy;
    private final String note;

    // public OrderItem(MenuItem menuItem, int quantity, Size size, boolean extraCheese, boolean spicy, String note) {
    //     this.menuItem = Objects.requireNonNull(menuItem, "Menu item cannot be null");
    //     if (quantity <= 0) {
    //         throw new IllegalArgumentException("Quantity must be positive");
    //     }
    //     this.quantity = quantity;
    //     this.size = size != null ? size : Size.MEDIUM;
    //     this.extraCheese = extraCheese;
    //     this.spicy = spicy;
    //     this.note = note != null ? note.trim() : "";
    // }
    private OrderItem(Builder builder) {
        // Required fields
        this.menuItem = Objects.requireNonNull(builder.menuItem, "Menu item cannot be null");
        if (builder.quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = builder.quantity;
        
        // Optional fields
        this.size = builder.size != null ? builder.size : Size.MEDIUM;
        this.extraCheese = builder.extraCheese;
        this.spicy = builder.spicy;
        this.note = builder.note != null ? builder.note.trim() : "";
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public Size getSize() {
        return size;
    }

    public boolean hasExtraCheese() {
        return extraCheese;
    }

    public boolean isSpicy() {
        return spicy;
    }

    public String getNote() {
        return note;
    }

    public double getUnitPrice() {
        double price = menuItem.getBasePrice() * size.getMultiplier();
        if (extraCheese) {
            price += EXTRA_CHEESE_PRICE;
        }
        return price;
    }

    public double getSubtotal() {
        return getUnitPrice() * quantity;
    }

    public String describeOptions() {
        StringBuilder options = new StringBuilder(size.name());
        if (extraCheese) {
            options.append(", extra cheese");
        }
        if (spicy) {
            options.append(", spicy");
        }
        if (!note.isEmpty()) {
            options.append(", note: ").append(note);
        }
        return options.toString();
    }

    @Override
    public String toString() {
        return String.format("%dx %-20s %-32s %8.2f",
                quantity,
                menuItem.getName(),
                describeOptions(),
                getSubtotal());
    }
    public static class Builder {
        // Required parameters
        private final MenuItem menuItem;
        private final int quantity;

        // Optional parameters initialized to their default values
        private Size size = Size.MEDIUM;
        private boolean extraCheese = false;
        private boolean spicy = false;
        private String note = "";

        //Absolute required fields constructor
        public Builder(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }

        //Setters for constructing the OrderItem object(Oprional fields)

        public Builder size(Size size) {
            //this.size = size != null ? size : Size.MEDIUM;
            this.size = size;
            return this;
        }

        public Builder extraCheese(boolean extraCheese) {
            this.extraCheese = extraCheese;
            return this;
        }

        public Builder spicy(boolean spicy) {
            this.spicy = spicy;
            return this;
        }

        public Builder note(String note) {
            
            //this.note = note != null ? note.trim() : "";
            this.note = note;
            return this;
        }

        //Returns the constructed OrderItem object(immutable)
        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}

