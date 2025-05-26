package com.yurii.coffeevan.coffeevan.model;

import com.yurii.coffeevan.coffeevan.model.Coffee;

public class GroundCoffee extends Coffee {
    public GroundCoffee(String name, int volume, double price, int weight, int quality, int quantity) {
        super(name, "Мелена", volume, price, weight, quality, quantity);
    }
}