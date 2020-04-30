package me.relavis.ravagersaddle;

import com.comphenix.protocol.ProtocolLibrary;
import me.relavis.ravagersaddle.events.*;
import me.relavis.ravagersaddle.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public final class RavagerSaddle extends JavaPlugin implements Listener {

    public static Double mspeed;
    public static Double mjumpspd;
    public static Boolean disablejump;
    public static Boolean fhead;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);


        Bukkit.getPluginManager().registerEvents(new SaddleClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamagedEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityHostileTargetEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityDismountEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitEvent(), this);

        FileConfiguration config = getConfig();
        mspeed = config.getDouble("movement-steer-speed");
        mjumpspd = config.getDouble("movement-jump-speed");
        disablejump = config.getBoolean("disable-entity-jump");
        fhead = config.getBoolean("follow-player-head");

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this,
                ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
            public void onPacketReceiving(PacketEvent event) {
                MountMoveListener.onMountEntityMove(event);
            }
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
