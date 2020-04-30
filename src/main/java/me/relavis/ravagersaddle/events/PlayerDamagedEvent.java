package me.relavis.ravagersaddle.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;

public class PlayerDamagedEvent implements Listener {

    @EventHandler
    public void onPlayerDamagedEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if(p.isInsideVehicle()) {
                if (Objects.requireNonNull(p.getVehicle()).getType() == EntityType.RAVAGER) {
                    p.leaveVehicle();
                }

            }
        }
    }
}
