package io.github.tavstaldev.ingotSpawner.model;

import org.bukkit.Material;

public class Ingot {
    private final Material material;
    private final int weight;
    private final double money;

    public Ingot(Material material, int weight, double money) {
        this.material = material;
        this.weight = weight;
        this.money = money;
    }

    public Material getMaterial() {
        return material;
    }

    public int getWeight() {
        return weight;
    }

    public double getMoney() {
        return money;
    }
}
