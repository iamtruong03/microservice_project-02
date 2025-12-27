package com.example.common.pattern;

/**
 * Builder Pattern Implementation
 * Dùng để tạo complex objects một cách dễ dàng
 * Ví dụ: Tạo Order với nhiều optional fields
 */
public class OrderBuilder {

    private String orderId;
    private String userId;
    private String status;
    private double totalPrice;
    private String shippingAddress;
    private String paymentMethod;
    private String notes;
    private int quantity;

    public OrderBuilder(String orderId, String userId) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = "PENDING";
    }

    public OrderBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public OrderBuilder withTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public OrderBuilder withShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }

    public OrderBuilder withPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderBuilder withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public OrderBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public Order build() {
        return new Order(
            this.orderId,
            this.userId,
            this.status,
            this.totalPrice,
            this.shippingAddress,
            this.paymentMethod,
            this.notes,
            this.quantity
        );
    }

    // Inner class
    public static class Order {
        public String orderId;
        public String userId;
        public String status;
        public double totalPrice;
        public String shippingAddress;
        public String paymentMethod;
        public String notes;
        public int quantity;

        public Order(String orderId, String userId, String status, double totalPrice,
                     String shippingAddress, String paymentMethod, String notes, int quantity) {
            this.orderId = orderId;
            this.userId = userId;
            this.status = status;
            this.totalPrice = totalPrice;
            this.shippingAddress = shippingAddress;
            this.paymentMethod = paymentMethod;
            this.notes = notes;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "orderId='" + orderId + '\'' +
                    ", userId='" + userId + '\'' +
                    ", status='" + status + '\'' +
                    ", totalPrice=" + totalPrice +
                    ", shippingAddress='" + shippingAddress + '\'' +
                    ", paymentMethod='" + paymentMethod + '\'' +
                    ", notes='" + notes + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }
}
