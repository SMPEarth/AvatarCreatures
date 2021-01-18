package me.relavis.avatarcreatures.events;

import com.palmergames.bukkit.towny.event.MobRemovalEvent;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class TownyMobRemoval implements Listener {

    DataHandler data = DataHandler.getInstance();

    @EventHandler
    public void onTownyMobRemove(MobRemovalEvent e) {
        UUID entityUUID = e.getEntity().getUniqueId();
        if (data.entityHasData(entityUUID)) {
            e.setCancelled(true);
        }
    }
}
