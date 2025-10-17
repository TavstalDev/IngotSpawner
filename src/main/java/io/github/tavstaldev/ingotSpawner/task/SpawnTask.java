package io.github.tavstaldev.ingotSpawner.task;

import io.github.tavstaldev.ingotSpawner.IngotSpawner;
import io.github.tavstaldev.ingotSpawner.model.Ingot;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SpawnTask extends BukkitRunnable {
    @Override
    public void run() {
        var spawnPoints = IngotSpawner.getLocations().data;
        for (Location location : spawnPoints) {
            for (Entity entity : location.getWorld().getEntities()) {
                if (!(entity instanceof Item item) || item.getLocation().distanceSquared(location) > 1.0)
                    continue;
                item.remove();
            }

            Ingot ingot = IngotSpawner.config().getRandomIngot();
            Item goldIngot = location.getWorld().dropItem(location, new ItemStack(ingot.getMaterial()));
            goldIngot.setVelocity(new Vector(0, 0, 0));
            goldIngot.setPickupDelay(0);
            goldIngot.setGravity(false);
            goldIngot.setPickupDelay(0);
        }
    }
}
