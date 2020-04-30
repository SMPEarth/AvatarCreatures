package me.relavis.ravagersaddle.events;

import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class MountEntityHostileTargetEvent implements Listener {

    @EventHandler
    public void onHostileTarget(EntityTargetEvent e) {
        if(e.getEntity() instanceof Ravager) {
            e.setCancelled(true);
        }
    }
}
