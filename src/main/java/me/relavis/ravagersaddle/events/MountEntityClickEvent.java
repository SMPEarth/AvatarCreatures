package me.relavis.ravagersaddle.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MountEntityClickEvent implements Listener {

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        Entity ent = e.getRightClicked();

        if(ent.getType() == EntityType.RAVAGER && ent.getPassengers().isEmpty()) {
            ent.addPassenger(p);
            Bukkit.broadcastMessage(ent.getUniqueId().toString());
        }
    }
}
