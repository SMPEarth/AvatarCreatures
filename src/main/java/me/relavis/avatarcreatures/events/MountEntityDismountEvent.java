package me.relavis.avatarcreatures.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class MountEntityDismountEvent implements Listener {
    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        try {
            Player p = (Player) e.getEntity();
        } catch (Exception exception) {
        }
    }
}
