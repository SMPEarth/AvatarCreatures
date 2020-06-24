package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuitEvent implements Listener {
    DataHandler data = DataHandler.getInstance();

    // For future use - clear+save player data on quit
    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {

    }
}
