package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.ConfigHandler;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;
import java.util.UUID;

public class SpawnItemClick implements Listener {

    final DataHandler data = DataHandler.getInstance();
    final ConfigHandler config = ConfigHandler.getInstance();

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();
        UUID playerUUID = player.getUniqueId();

        if (action == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            if (player.getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(config.getAppaSpawnItem())) && player.hasPermission("avatarcreatures.appa.spawn")) {
                if (player.hasPermission("avatarcreatures.appa.spawn")) {
                    createEntity(event, playerUUID, EntityType.RAVAGER);
                    event.setCancelled(true);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission to spawn an Appa.");
                }
            }
        }
    }

    public void createEntity(PlayerInteractEvent event, UUID playerUUID, EntityType type) {
        Player player = event.getPlayer();
        Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        UUID oldEntityUUID = data.getEntityUUID(playerUUID, EntityType.RAVAGER);
        boolean isAlive;
        if(oldEntityUUID != null) {
            isAlive = data.isAlive(playerUUID, oldEntityUUID);
        } else {
            isAlive = false;
        }
        if (isAlive) { // Check if player's mount entity is already spawned in
            player.sendMessage(ChatColor.RED + "You already have an Appa! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
        } else { // If it isn't:
            if (data.entityExists(playerUUID, type)) { // Check if the user has already had a mount entity. If so:
                player.sendMessage(ChatColor.RED + "Your Appa is away! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
            } else {
                player.getWorld().spawn(loc.add(0.0, 1.0, 0.0), Ravager.class, entity -> {
                    entity.setRemoveWhenFarAway(false);
                    entity.setGravity(false);
                    String playerName = player.getName();
                    entity.setCustomNameVisible(true);
                    entity.setCustomName(playerName + "'s Appa");
                    player.getInventory().setItemInMainHand(null);
                    UUID entityUUID = entity.getUniqueId();
                    data.addEntityToData(playerUUID, -1, type, playerName + "'s Appa", entityUUID, true);

                });
            }
        }
    }

}
