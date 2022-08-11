package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoin implements Listener {
    final DataHandler data = DataHandler.getInstance();

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        data.initializePlayerData(e.getPlayer());
    }

}
