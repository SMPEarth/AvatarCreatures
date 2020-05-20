package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MountEntityDamagedEvent implements Listener {
    AvatarCreatures plugin = AvatarCreatures.getPlugin(AvatarCreatures.class);
    boolean appaDismountWhenAppaHurt = plugin.getConfig().getBoolean("appa.unmount-when-appa-damaged");

    @EventHandler
    public void onMountEntityDamagedEvent(EntityDamageEvent e) {
        if (!e.getEntity().getPassengers().isEmpty() && e.getEntity().getPassengers() instanceof Player) {
            if (e.getEntity() instanceof Ravager && appaDismountWhenAppaHurt) {
                Player p = (Player) e.getEntity().getPassengers();
                p.leaveVehicle();
            }
        }
    }
}

