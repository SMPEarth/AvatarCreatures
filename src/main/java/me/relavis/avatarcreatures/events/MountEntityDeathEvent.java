package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public class MountEntityDeathEvent implements Listener {

    DataHandler data = DataHandler.getInstance();

    @EventHandler
    public void onMountEntityDeath(EntityDeathEvent e) {
        UUID entityUUID = e.getEntity().getUniqueId();
        if (data.isOwned(entityUUID)) {
            UUID playerUUID = data.getEntityOwnerUUID(entityUUID);
            Player player = Bukkit.getPlayer(playerUUID);

            data.removeEntityFromData(entityUUID);
            e.getDrops().clear();
            if (player.isOnline()) {
                player.sendMessage(ChatColor.BLUE + "Your Appa has died.");
            }
        }
    }


}
