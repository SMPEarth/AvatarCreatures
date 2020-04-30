package me.relavis.ravagersaddle.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;
import java.util.UUID;

public class SaddleClickEvent implements Listener {

    @EventHandler
    public void onSaddleRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();

        if(action == Action.RIGHT_CLICK_BLOCK && player.getInventory().getItemInMainHand().getType() == Material.SADDLE){
            Location loc = Objects.requireNonNull(e.getClickedBlock()).getLocation();
            player.getWorld().spawn(loc.add(0.0, 1.0, 0.0), Ravager.class, entity -> {
                entity.setRemoveWhenFarAway(false);
                String entityuuid = entity.getUniqueId().toString();
                Bukkit.broadcastMessage(entityuuid);
            });
            player.getInventory().setItemInMainHand(null);
        }
    }


}
