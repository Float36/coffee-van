package com.yurii.coffeevan.coffeevan.model;

public enum QualityCategory {
    LOW_QUALITY("Низька якість", 1, 20),
    BELOW_AVERAGE("Нижче середньої", 21, 40),
    AVERAGE("Середня якість", 41, 60),
    GOOD("Хороша якість", 61, 80),
    PREMIUM("Преміум якість", 81, 100);

    private final String displayName;
    private final int minValue;
    private final int maxValue;

    QualityCategory(String displayName, int minValue, int maxValue) {
        this.displayName = displayName;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public static QualityCategory fromQuality(int quality) {
        for (QualityCategory category : values()) {
            if (quality >= category.minValue && quality <= category.maxValue) {
                return category;
            }
        }
        return LOW_QUALITY; // default category
    }
} 