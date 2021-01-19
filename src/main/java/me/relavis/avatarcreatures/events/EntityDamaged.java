package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.ConfigHandler;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamaged implements Listener {
    ConfigHandler config = ConfigHandler.getInstance();

    @EventHandler
    public void onMountEntityDamagedEvent(EntityDamageEvent e) {
        if (!e.getEntity().getPassengers().isEmpty() && e.getEntity().getPassengers() instanceof Player) {
            if (e.getEntity() instanceof Ravager && config.getAppaUnmountWhenAppaDamaged()) {
                Player p = (Player) e.getEntity().getPassengers();
                p.leaveVehicle();
            }
        }
    }
}

