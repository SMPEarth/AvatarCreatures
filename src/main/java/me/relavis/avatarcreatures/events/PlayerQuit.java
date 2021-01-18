package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuit implements Listener {
    DataHandler data = DataHandler.getInstance();

    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        data.unloadPlayerData(e.getPlayer());
    }
}
