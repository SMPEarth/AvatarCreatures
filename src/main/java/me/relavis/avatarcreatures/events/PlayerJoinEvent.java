package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener {
    DataHandler data = new DataHandler();


    // For future use - initialize player data on server join
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {


    }

}
