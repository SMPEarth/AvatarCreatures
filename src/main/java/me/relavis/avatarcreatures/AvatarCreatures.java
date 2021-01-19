package me.relavis.avatarcreatures;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import me.relavis.avatarcreatures.events.*;
import me.relavis.avatarcreatures.listeners.MountMoveListener;
import me.relavis.avatarcreatures.util.CommandInitializer;
import me.relavis.avatarcreatures.util.ConfigHandler;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AvatarCreatures extends JavaPlugin implements Listener {


    @Getter
    private static AvatarCreatures instance;

    ConfigHandler config = new ConfigHandler();

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        config.configSetup();

        DataHandler data = new DataHandler();
        data.dataSetup();

        Bukkit.getPluginManager().registerEvents(new EntityDamaged(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeath(), this);
        Bukkit.getPluginManager().registerEvents(new EntityHostileTarget(), this);
        Bukkit.getPluginManager().registerEvents(new EntityMount(), this);
        Bukkit.getPluginManager().registerEvents(new EntityUnmount(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamaged(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnItemClick(), this);
        Bukkit.getPluginManager().registerEvents(new TownyMobRemoval(), this);

        new CommandInitializer();

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
