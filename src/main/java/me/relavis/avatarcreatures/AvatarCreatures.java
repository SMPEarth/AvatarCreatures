package me.relavis.avatarcreatures;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.relavis.avatarcreatures.commands.AppaCommand;
import me.relavis.avatarcreatures.events.*;
import me.relavis.avatarcreatures.listeners.MountMoveListener;
import me.relavis.avatarcreatures.util.ConfigHandler;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AvatarCreatures extends JavaPlugin implements Listener {

    private static AvatarCreatures instance;
    ConfigHandler config = new ConfigHandler();


    public static AvatarCreatures getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        config.configSetup();

        DataHandler data = new DataHandler();
        data.dataSetup();

        Bukkit.getPluginManager().registerEvents(new SpawnItemClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamagedEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityDamagedEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityHostileTargetEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityMountEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityUnmountEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), this);


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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DataHandler data = DataHandler.getInstance();
        data.unloadOnlinePlayers();
    }

}
