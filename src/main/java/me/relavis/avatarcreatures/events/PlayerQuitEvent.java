package me.relavis.avatarcreatures.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        try {
            Player p = e.getPlayer();
        } catch (Exception exception) {
        }
    }
}
