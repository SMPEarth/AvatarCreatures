package me.relavis.avatarcreatures.events;

import org.bukkit.entity.Golem;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class GolemTargetEvent implements Listener {

    @EventHandler
    public void onHostileTarget(EntityTargetEvent e) {
        if(e.getEntity() instanceof Golem && e.getTarget() instanceof Ravager) {
            e.setCancelled(true);
        }
    }

}
