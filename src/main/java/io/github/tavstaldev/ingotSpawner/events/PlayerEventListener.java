package io.github.tavstaldev.ingotSpawner.events;

public class PlayerEventListener implements Listener {
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        if (item.getItemStack().getType() == Material.GOLD_INGOT) {
            event.setCancelled(true);
            item.remove();
            String command = this.configManager.getRewardCommand().replace("{player}", event.getPlayer().getName());
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)command);
        }
    }
}
