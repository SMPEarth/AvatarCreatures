package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.ConfigHandler;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamaged implements Listener {
    ConfigHandler config = ConfigHandler.getInstance();

    @EventHandler
    public void onPlayerDamagedEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (p.isInsideVehicle()) {
                if (p.getVehicle() instanceof Ravager && config.getAppaUnmountWhenPlayerDamaged()) {
                    p.leaveVehicle();
                }

            }
        }
    }
}
