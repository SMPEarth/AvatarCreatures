package me.relavis.avatarcreatures;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.relavis.avatarcreatures.commands.AppaCommand;
import me.relavis.avatarcreatures.events.*;
import me.relavis.avatarcreatures.listeners.MountMoveListener;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class AvatarCreatures extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        int configVersion = getConfig().getInt("version");
        if (configVersion != 1) {
            getLogger().severe("Error: Your config is outdated or corrupted. Please delete your config and restart the server. Disabling AvatarCreatures...");
            Bukkit.getPluginManager().disablePlugin(this);
        } else if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().severe("Error: This plugin requires ProtocolLib. Disabling AvatarCreatures...");
        } else {
            getLogger().log(Level.INFO, "Configuration initialization successful.");

            DataHandler data = new DataHandler();
            data.dataSetup();

            Bukkit.getPluginManager().registerEvents(new SpawnItemClickEvent(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerDamagedEvent(), this);
            Bukkit.getPluginManager().registerEvents(new MountEntityDamagedEvent(), this);
            Bukkit.getPluginManager().registerEvents(new MountEntityHostileTargetEvent(), this);
            Bukkit.getPluginManager().registerEvents(new MountEntityDismountEvent(), this);
            Bukkit.getPluginManager().registerEvents(new MountEntityClickEvent(), this);
            Bukkit.getPluginManager().registerEvents(new MountEntityDeathEvent(), this);
            //Bukkit.getPluginManager().registerEvents(new PlayerQuitEvent(), this);
            //Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), this);


            Objects.requireNonNull(this.getCommand("appa")).setExecutor(new AppaCommand());

            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this,
                    ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
                public void onPacketReceiving(PacketEvent event) {
                    MountMoveListener.onMountEntitySteer(event);
                }
            });
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this,
                    ListenerPriority.HIGHEST, PacketType.Play.Client.VEHICLE_MOVE) {
                public void onPacketReceiving(PacketEvent event) {
                    MountMoveListener.onMountEntityMove(event);
                }
            });

            int pluginId = 7715;
            Metrics metrics = new Metrics(this, pluginId);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
