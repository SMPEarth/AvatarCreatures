package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamagedEvent implements Listener {
    AvatarCreatures plugin = AvatarCreatures.getPlugin(AvatarCreatures.class);
    boolean appaDismountWhenPlayerHurt = plugin.getConfig().getBoolean("appa.unmount-when-player-damaged");

    @EventHandler
    public void onPlayerDamagedEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (p.isInsideVehicle()) {
                if (p.getVehicle() instanceof Ravager && appaDismountWhenPlayerHurt) {
                    p.leaveVehicle();
                }

            }
        }
    }
}
