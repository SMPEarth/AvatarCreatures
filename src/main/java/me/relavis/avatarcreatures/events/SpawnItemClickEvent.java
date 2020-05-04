package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.AvatarCreatures;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;
import java.util.UUID;

public class SpawnItemClickEvent implements Listener {

    DataHandler data = new DataHandler();
    AvatarCreatures plugin = AvatarCreatures.getPlugin(AvatarCreatures.class);

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        Action action = e.getAction();

        UUID playerUUID = player.getUniqueId();

        if (action == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND && player.getInventory().getItemInMainHand().getType() == Material.SADDLE) {
            createEntity(e, playerUUID, "RAVAGER");
            e.setCancelled(true);
            return;
        }
    }

    public void createEntity(PlayerInteractEvent e, UUID playerUUID, String type) {
        Player player = e.getPlayer();
        Location loc = Objects.requireNonNull(e.getClickedBlock()).getLocation();
        boolean isAlive = data.isAlive(playerUUID, type);

        if (isAlive) { // Check if player's mount entity is already spawned in
            player.sendMessage(ChatColor.RED + "You already have an Appa! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
            return;
        } else { // If it isn't:
            if (data.entityExists(playerUUID, type)) { // Check if the user has already had a mount entity. If so:
                /*
                player.getWorld().spawn(loc.add(0.0, 1.0, 0.0), Ravager.class, entity -> { // Spawn entity
                    entity.setRemoveWhenFarAway(false);
                    entity.setCustomNameVisible(true);
                    entity.setCustomName(data.getEntityName(playerUUID, type)); // Change entity name to whatever is in DB
                    UUID entityUUID = entity.getUniqueId(); // Get new entity UUID
                    data.updateEntityUUID(playerUUID, entityUUID); // Update entity UUID to new UUID
                    data.setAlive(entityUUID, true); // Set entity's state
                    player.getInventory().setItemInMainHand(null); // Remove saddle from inventory
                    e.setCancelled(true);
                    return;
                });
                 */
                player.sendMessage(ChatColor.RED + "Your Appa is away! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
            } else {
                player.getWorld().spawn(loc.add(0.0, 1.0, 0.0), Ravager.class, entity -> {
                    entity.setRemoveWhenFarAway(false);
                    String playerName = player.getName();
                    entity.setCustomNameVisible(true);
                    entity.setCustomName(playerName + "'s Appa");
                    player.getInventory().setItemInMainHand(null);
                    UUID entityUUID = entity.getUniqueId();
                    data.addEntityToData(playerName, playerUUID, entityUUID, type);
                    return;
                });
            }
        }
    }

}
