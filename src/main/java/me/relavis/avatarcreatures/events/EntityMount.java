package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class EntityMount implements Listener {

    final DataHandler data = DataHandler.getInstance();

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Entity entity = e.getRightClicked();
        UUID entityUUID = entity.getUniqueId();
        if (entity.getType() == EntityType.RAVAGER && entity.getPassengers().isEmpty() && e.getHand().equals(EquipmentSlot.HAND)) {
            if (data.isOwner(playerUUID, entityUUID) || player.hasPermission("avatarcreatures.appa.ride.others")) {
                if (player.hasPermission("avatarcreatures.appa.ride")) {
                    entity.addPassenger(player);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "You do not have the ability to ride an Appa.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "That's not your Appa!");
            }
        }
    }
}
