package com.example.common.pattern;

/**
 * Factory Pattern Implementation
 * Dùng để tạo các objects khác nhau dựa trên input
 * Ví dụ: Tạo PaymentProcessor dựa trên payment method
 */
public class PaymentProcessorFactory {

    public static PaymentProcessor createPaymentProcessor(String paymentMethod) {
        switch (paymentMethod.toUpperCase()) {
            case "CREDIT_CARD":
                return new CreditCardProcessor();
            case "DEBIT_CARD":
                return new DebitCardProcessor();
            case "PAYPAL":
                return new PayPalProcessor();
            case "BANK_TRANSFER":
                return new BankTransferProcessor();
            case "CRYPTO":
                return new CryptoProcessor();
            default:
                throw new IllegalArgumentException("Unknown payment method: " + paymentMethod);
        }
    }

    // Payment Processor Interface
    public interface PaymentProcessor {
        boolean process(double amount, String accountId);
        String getPaymentMethod();
    }

    // Credit Card Processor
    public static class CreditCardProcessor implements PaymentProcessor {
        @Override
        public boolean process(double amount, String accountId) {
            System.out.println("💳 Processing credit card payment: $" + amount);
            // Validate card
            // Charge card
            // Return transaction ID
            return true;
        }

        @Override
        public String getPaymentMethod() {
            return "CREDIT_CARD";
        }
    }

    // Debit Card Processor
    public static class DebitCardProcessor implements PaymentProcessor {
        @Override
        public boolean process(double amount, String accountId) {
            System.out.println("💳 Processing debit card payment: $" + amount);
            // Validate card
            // Charge account
            return true;
        }

        @Override
        public String getPaymentMethod() {
            return "DEBIT_CARD";
        }
    }

    // PayPal Processor
    public static class PayPalProcessor implements PaymentProcessor {
        @Override
        public boolean process(double amount, String accountId) {
            System.out.println("🅿️ Processing PayPal payment: $" + amount);
            // Call PayPal API
            // Return status
            return true;
        }

        @Override
        public String getPaymentMethod() {
            return "PAYPAL";
        }
    }

    // Bank Transfer Processor
    public static class BankTransferProcessor implements PaymentProcessor {
        @Override
        public boolean process(double amount, String accountId) {
            System.out.println("🏦 Processing bank transfer: $" + amount);
            // Initiate bank transfer
            // Track transaction
            return true;
        }

        @Override
        public String getPaymentMethod() {
            return "BANK_TRANSFER";
        }
    }

    // Crypto Processor
    public static class CryptoProcessor implements PaymentProcessor {
        @Override
        public boolean process(double amount, String accountId) {
            System.out.println("₿ Processing crypto payment: $" + amount);
            // Connect to blockchain
            // Process transaction
            return true;
        }

        @Override
        public String getPaymentMethod() {
            return "CRYPTO";
        }
    }
}
