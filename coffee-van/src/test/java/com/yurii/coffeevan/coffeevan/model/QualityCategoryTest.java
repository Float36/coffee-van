package com.yurii.coffeevan.coffeevan.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Quality Category Tests")
class QualityCategoryTest {

    @Test
    @DisplayName("Should have correct display names")
    void testDisplayNames() {
        assertEquals("Низька якість", QualityCategory.LOW_QUALITY.getDisplayName());
        assertEquals("Нижче середньої", QualityCategory.BELOW_AVERAGE.getDisplayName());
        assertEquals("Середня якість", QualityCategory.AVERAGE.getDisplayName());
        assertEquals("Хороша якість", QualityCategory.GOOD.getDisplayName());
        assertEquals("Преміум якість", QualityCategory.PREMIUM.getDisplayName());
    }

    @Test
    @DisplayName("Should have correct value ranges")
    void testValueRanges() {
        assertEquals(1, QualityCategory.LOW_QUALITY.getMinValue());
        assertEquals(20, QualityCategory.LOW_QUALITY.getMaxValue());
        
        assertEquals(21, QualityCategory.BELOW_AVERAGE.getMinValue());
        assertEquals(40, QualityCategory.BELOW_AVERAGE.getMaxValue());
        
        assertEquals(41, QualityCategory.AVERAGE.getMinValue());
        assertEquals(60, QualityCategory.AVERAGE.getMaxValue());
        
        assertEquals(61, QualityCategory.GOOD.getMinValue());
        assertEquals(80, QualityCategory.GOOD.getMaxValue());
        
        assertEquals(81, QualityCategory.PREMIUM.getMinValue());
        assertEquals(100, QualityCategory.PREMIUM.getMaxValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 20})
    @DisplayName("Should identify low quality correctly")
    void testLowQualityRange(int quality) {
        assertEquals(QualityCategory.LOW_QUALITY, QualityCategory.fromQuality(quality));
    }

    @ParameterizedTest
    @ValueSource(ints = {21, 30, 40})
    @DisplayName("Should identify below average quality correctly")
    void testBelowAverageRange(int quality) {
        assertEquals(QualityCategory.BELOW_AVERAGE, QualityCategory.fromQuality(quality));
    }

    @ParameterizedTest
    @ValueSource(ints = {41, 50, 60})
    @DisplayName("Should identify average quality correctly")
    void testAverageRange(int quality) {
        assertEquals(QualityCategory.AVERAGE, QualityCategory.fromQuality(quality));
    }

    @ParameterizedTest
    @ValueSource(ints = {61, 70, 80})
    @DisplayName("Should identify good quality correctly")
    void testGoodRange(int quality) {
        assertEquals(QualityCategory.GOOD, QualityCategory.fromQuality(quality));
    }

    @ParameterizedTest
    @ValueSource(ints = {81, 90, 100})
    @DisplayName("Should identify premium quality correctly")
    void testPremiumRange(int quality) {
        assertEquals(QualityCategory.PREMIUM, QualityCategory.fromQuality(quality));
    }

    @Test
    @DisplayName("Should handle edge cases correctly")
    void testEdgeCases() {
        // Test values at boundaries
        assertEquals(QualityCategory.LOW_QUALITY, QualityCategory.fromQuality(0));
        assertEquals(QualityCategory.LOW_QUALITY, QualityCategory.fromQuality(-1));
        assertEquals(QualityCategory.LOW_QUALITY, QualityCategory.fromQuality(1));
        assertEquals(QualityCategory.PREMIUM, QualityCategory.fromQuality(100));
        assertEquals(QualityCategory.LOW_QUALITY, QualityCategory.fromQuality(101));
    }
} 