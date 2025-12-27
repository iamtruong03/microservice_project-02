package com.example.common.pattern;

/**
 * Strategy Pattern Implementation
 * Dùng để encapsulate các algorithms khác nhau
 * Ví dụ: Các discounting strategies khác nhau
 */
public class DiscountStrategy {

    // Strategy Interface
    public interface DiscountCalculator {
        double calculateDiscount(double originalPrice);
        String getStrategyName();
    }

    // No Discount Strategy
    public static class NoDiscountStrategy implements DiscountCalculator {
        @Override
        public double calculateDiscount(double originalPrice) {
            return 0;
        }

        @Override
        public String getStrategyName() {
            return "NO_DISCOUNT";
        }
    }

    // Fixed Amount Discount Strategy
    public static class FixedAmountDiscountStrategy implements DiscountCalculator {
        private final double discountAmount;

        public FixedAmountDiscountStrategy(double discountAmount) {
            this.discountAmount = discountAmount;
        }

        @Override
        public double calculateDiscount(double originalPrice) {
            double discount = Math.min(discountAmount, originalPrice);
            return discount;
        }

        @Override
        public String getStrategyName() {
            return "FIXED_AMOUNT_DISCOUNT";
        }
    }

    // Percentage Discount Strategy
    public static class PercentageDiscountStrategy implements DiscountCalculator {
        private final double discountPercentage;

        public PercentageDiscountStrategy(double discountPercentage) {
            if (discountPercentage < 0 || discountPercentage > 100) {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }
            this.discountPercentage = discountPercentage;
        }

        @Override
        public double calculateDiscount(double originalPrice) {
            return originalPrice * (discountPercentage / 100);
        }

        @Override
        public String getStrategyName() {
            return "PERCENTAGE_DISCOUNT";
        }
    }

    // Volume Discount Strategy
    public static class VolumeDiscountStrategy implements DiscountCalculator {
        private final int minimumQuantity;
        private final double discountPercentage;

        public VolumeDiscountStrategy(int minimumQuantity, double discountPercentage) {
            this.minimumQuantity = minimumQuantity;
            this.discountPercentage = discountPercentage;
        }

        @Override
        public double calculateDiscount(double originalPrice) {
            // Simplified: assume quantity is passed separately
            return originalPrice * (discountPercentage / 100);
        }

        @Override
        public String getStrategyName() {
            return "VOLUME_DISCOUNT";
        }
    }

    // Loyalty Discount Strategy
    public static class LoyaltyDiscountStrategy implements DiscountCalculator {
        private final int loyaltyPoints;

        public LoyaltyDiscountStrategy(int loyaltyPoints) {
            this.loyaltyPoints = loyaltyPoints;
        }

        @Override
        public double calculateDiscount(double originalPrice) {
            // 1 loyalty point = $0.01 discount
            double pointsDiscount = loyaltyPoints * 0.01;
            return Math.min(pointsDiscount, originalPrice * 0.5); // Max 50% discount
        }

        @Override
        public String getStrategyName() {
            return "LOYALTY_DISCOUNT";
        }
    }

    // Seasonal Discount Strategy
    public static class SeasonalDiscountStrategy implements DiscountCalculator {
        private final String season;
        private final double discountPercentage;

        public SeasonalDiscountStrategy(String season, double discountPercentage) {
            this.season = season;
            this.discountPercentage = discountPercentage;
        }

        @Override
        public double calculateDiscount(double originalPrice) {
            return originalPrice * (discountPercentage / 100);
        }

        @Override
        public String getStrategyName() {
            return "SEASONAL_DISCOUNT_" + season.toUpperCase();
        }
    }

    // Price Calculator using Strategy
    public static class PriceCalculator {
        private DiscountCalculator discountStrategy;

        public PriceCalculator(DiscountCalculator discountStrategy) {
            this.discountStrategy = discountStrategy;
        }

        public void setDiscountStrategy(DiscountCalculator discountStrategy) {
            this.discountStrategy = discountStrategy;
        }

        public double calculateFinalPrice(double originalPrice) {
            double discount = discountStrategy.calculateDiscount(originalPrice);
            double finalPrice = originalPrice - discount;
            System.out.printf("Strategy: %s | Original: $%.2f | Discount: $%.2f | Final: $%.2f%n",
                    discountStrategy.getStrategyName(), originalPrice, discount, finalPrice);
            return finalPrice;
        }
    }
}
