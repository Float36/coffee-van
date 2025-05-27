package com.yurii.coffeevan.coffeevan.model;

import java.util.ArrayList;
import java.util.List;
import com.yurii.coffeevan.coffeevan.model.*;

public class Van {

    private static final double MAX_CAPACITY = 5000000.0; // у мілілітрах (мл)
    private List<Coffee> coffeeList;
    private double currentVolume;

    public Van() {
        this.coffeeList = new ArrayList<>();
        this.currentVolume = 0.0;
    }

    public boolean addCoffee(Coffee coffee) {
        if (getTotalVolume() + coffee.getCoffeeVolume() <= MAX_CAPACITY) {
            coffeeList.add(coffee);
            currentVolume += coffee.getCoffeeVolume();
            return true;
        }
        return false;
    }

    public boolean removeCoffee(Coffee coffee) {
        if (coffeeList.remove(coffee)) {
            currentVolume -= coffee.getCoffeeVolume();
            return true;
        }
        return false;
    }

    public void clear() {
        coffeeList.clear();
        currentVolume = 0.0;
    }

    public List<Coffee> getAllCoffee() {
        return new ArrayList<>(coffeeList); // повертаємо копію для безпеки
    }

    // Загальна об'єм усієї кави
    public int getTotalVolume() {
        int totalVolume = 0;
        for (Coffee coffee : coffeeList){
            totalVolume += coffee.getCoffeeVolume();
        }
        return totalVolume;
    }

    public double getCurrentVolume() {
        return currentVolume;
    }

    public double getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public List<Coffee> filterByType(String type) {
        return coffeeList.stream()
                .filter(c -> c.getType().equalsIgnoreCase(type))
                .toList();
    }

    public List<Coffee> filterByQuality(int minQuality) {
        return coffeeList.stream()
                .filter(c -> c.getQuality() >= minQuality)
                .toList();
    }

    public List<Coffee> sortByPricePerWeight() {
        return coffeeList.stream()
                .sorted((c1, c2) -> Double.compare(c1.getPrice() / c1.getWeight(), c2.getPrice() / c2.getWeight()))
                .toList();
    }

    public List<Coffee> sortByQuality() {
        return coffeeList.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getQuality(), c1.getQuality()))
                .toList();
    }
}
