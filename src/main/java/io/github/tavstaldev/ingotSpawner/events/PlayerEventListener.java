package io.github.tavstaldev.ingotSpawner.events;

import io.github.tavstaldev.ingotSpawner.IngotSpawner;
import io.github.tavstaldev.ingotSpawner.util.EconomyUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.Map;

/**
 * Listener class for handling player-related events.
 * Specifically listens for item pickup events and processes
 * custom logic for ingots defined in the plugin configuration.
 */
public class PlayerEventListener implements Listener {

    /**
     * Registers this listener with the Bukkit plugin manager.
     * This method should be called during plugin initialization.
     */
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, IngotSpawner.Instance);
    }

    /**
     * Event handler for the EntityPickupItemEvent.
     * <p>
     * This method checks if the entity picking up the item is a player and if the
     * picked-up item corresponds to a custom ingot defined in the plugin configuration.
     * If it does:
     * - Cancels the pickup event.
     * - Removes the item from the world.
     * - Deposits the corresponding monetary value into the player's account.
     * - Sends a localized message to the player about the ingot pickup.
     * </p>
     *
     * @param event The EntityPickupItemEvent triggered when an entity picks up an item.
     */
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        // Check if the entity is a player
        if (!(event.getEntity() instanceof Player player))
            return;

        // Get the item being picked up
        Item item = event.getItem();

        // Retrieve the custom ingot configuration for the item's material
        var ingot = IngotSpawner.config().getIngotByMaterial(item.getItemStack().getType());
        if (ingot == null)
            return; // Exit if the item is not a custom ingot

        // Cancel the pickup event and remove the item from the world
        event.setCancelled(true);
        item.remove();

        // Deposit the ingot's monetary value into the player's account
        EconomyUtils.deposit(player, ingot.money());

        // Send a localized message to the player about the ingot pickup
        IngotSpawner.Instance.sendLocalizedMsg(player, "General.IngotPickup", Map.of(
                "value", String.valueOf(ingot.money())
        ));
    }
}