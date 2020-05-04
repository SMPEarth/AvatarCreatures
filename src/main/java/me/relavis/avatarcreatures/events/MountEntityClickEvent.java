package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class MountEntityClickEvent implements Listener {

    DataHandler data = new DataHandler();

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Entity entity = e.getRightClicked();
        UUID entityUUID = entity.getUniqueId();
        if(entity.getType() == EntityType.RAVAGER && entity.getPassengers().isEmpty()) {
            if(data.isOwner(playerUUID, entityUUID)){
                entity.addPassenger(player);
            } else {
                player.sendMessage(ChatColor.RED + "That's not your Appa!");
            }
        }
    }
}
