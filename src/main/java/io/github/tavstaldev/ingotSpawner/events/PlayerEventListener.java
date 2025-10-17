package io.github.tavstaldev.ingotSpawner.events;

import io.github.tavstaldev.ingotSpawner.IngotSpawner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerEventListener implements Listener {

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, IngotSpawner.Instance);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        var ingot = IngotSpawner.config().getIngotByMaterial(item.getItemStack().getType());
        if (ingot == null)
            return;

        event.setCancelled(true);
        item.remove();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("eco give %s %.2f", event.getPlayer().getName(), ingot.getMoney()));
    }
}
