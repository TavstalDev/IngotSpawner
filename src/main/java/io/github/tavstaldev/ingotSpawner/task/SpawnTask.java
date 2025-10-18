package io.github.tavstaldev.ingotSpawner.task;

import io.github.tavstaldev.ingotSpawner.IngotSpawner;
import io.github.tavstaldev.ingotSpawner.model.Ingot;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * A task that handles the spawning of custom ingots at predefined locations.
 * <p>
 * This task is executed periodically and performs the following actions:
 * - Removes any existing items near the spawn points to prevent clutter.
 * - Spawns a new custom ingot at each spawn point with specific properties.
 * </p>
 */
public class SpawnTask extends BukkitRunnable {

    /**
     * The main logic of the task, executed on each run.
     * <p>
     * This method iterates through all predefined spawn points and:
     * - Removes items within a 1-block radius of the spawn point.
     * - Retrieves a random custom ingot from the configuration.
     * - Spawns the ingot at the spawn point with no velocity, no gravity, and immediate pickup availability.
     * </p>
     */
    @Override
    public void run() {
        // Retrieve the list of spawn points from the plugin's configuration
        var spawnPoints = IngotSpawner.getLocations().data;

        // Iterate through each spawn point
        for (Location location : spawnPoints) {
            // Remove items near the spawn point to prevent clutter
            for (Entity entity : location.getWorld().getEntities()) {
                if (!(entity instanceof Item item) || item.getLocation().distanceSquared(location) > 1.0)
                    continue;
                item.remove();
            }

            // Retrieve a random custom ingot from the configuration
            Ingot ingot = IngotSpawner.config().getRandomIngot();

            // Spawn the ingot at the spawn point
            Item goldIngot = location.getWorld().dropItem(location, new ItemStack(ingot.material()));
            goldIngot.setVelocity(new Vector(0, 0, 0)); // Set no velocity
            goldIngot.setPickupDelay(0); // Allow immediate pickup
            goldIngot.setGravity(false); // Disable gravity
            goldIngot.setPickupDelay(0); // Ensure immediate pickup
        }
    }
}