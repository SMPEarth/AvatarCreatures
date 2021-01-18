package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.AvatarCreatures;
import me.relavis.avatarcreatures.util.ConfigHandler;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class MountEntityUnmount implements Listener {

    ConfigHandler config = ConfigHandler.getInstance();
    DataHandler data = DataHandler.getInstance();

    @EventHandler
    public void onEntityUnmount(EntityDismountEvent e) {
        if (e.getEntity() instanceof CraftPlayer) {
            Player player = (Player) e.getEntity();
            Entity entity = e.getDismounted();
            switch (entity.getType()) {
                case RAVAGER:
                    if (Boolean.parseBoolean(config.getAppaConfig("despawnAfterUnmount"))) {
                        Bukkit.getServer().getScheduler().runTaskLater(AvatarCreatures.getInstance(), () -> {
                            entity.remove();
                            data.setAlive(player.getUniqueId(), entity.getUniqueId(), false);
                            player.sendMessage(ChatColor.GREEN + "Your Appa flies away.");
                        }, Integer.parseInt(config.getAppaConfig("despawnTime")));
                    }
            }

        }
    }
}
