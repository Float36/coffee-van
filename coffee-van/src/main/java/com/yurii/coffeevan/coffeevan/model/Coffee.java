package com.yurii.coffeevan.coffeevan.model;

public class Coffee {
    protected String name;
    protected String type;
    protected int volume;
    protected double price;
    protected int weight;
    protected int quality;
    protected int quantity;

    public Coffee(String name, String type, int volume, double price, int weight, int quality, int quantity) {
        this.name = name;
        this.type = type;
        this.volume = volume;
        this.price = price;
        this.weight = weight;
        this.quality = quality;
        this.quantity= quantity;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getVolume() { return volume; }
    public double getPrice() { return price; }
    public int getWeight() { return weight; }
    public int getQuality() { return quality; }
    public int getQuantity() { return quantity; }

    // Загальний об'єм кави разом з кількістю
    public int getCoffeeVolume(){
        return this.quantity * this.volume;
    }

    // Сеттери
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setVolume(int volume) { this.volume = volume; }
    public void setPrice(double price) { this.price = price; }
    public void setWeight(int weight) { this.weight = weight; }
    public void setQuality(int quality) { this.quality = quality; }



    public double getPriceToWeightRatio(){
        return weight == 0 ? 0 : price / weight;
    }
}
